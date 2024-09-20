package com.stackoverflow.controller;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.entity.Question;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.VoteService;
import com.stackoverflow.service.impl.HtmlUtils;
import com.stackoverflow.service.impl.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final UserServiceImpl userService;
    private final HtmlUtils htmlUtils;
    private final ModelMapper modelMapper;
    private final VoteService voteService;

    public QuestionController(QuestionService questionService, UserServiceImpl userService, HtmlUtils htmlUtils, ModelMapper modelMapper, VoteService voteService) {
        this.questionService = questionService;
        this.userService = userService;
        this.htmlUtils = htmlUtils;
        this.modelMapper = modelMapper;
        this.voteService = voteService;
    }

    @GetMapping
    public String getAllQuestions(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                  Model model) {
        Page<QuestionDetailsDTO> questionsPage = questionService.getAllQuestions(page,size,sort);
        List<QuestionDetailsDTO> questions = questionsPage.getContent();
        int totalPages = questionsPage.getTotalPages();

        model.addAttribute("questions", questions);
        model.addAttribute("HtmlUtils", htmlUtils);
        if (userService.isUserLoggedIn()) {
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUserOrNull(), UserDetailsDTO.class));
        } else {
            model.addAttribute("loggedIn", null);
        }
        model.addAttribute("users", null);
        model.addAttribute("tags", null);
        model.addAttribute("current_page", page);
        model.addAttribute("total_pages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);

        return "dashboard";
    }

    @GetMapping("/{id}")
    public String getQuestionById(@PathVariable("id") Long questionId, Model model) {
        QuestionDetailsDTO question = questionService.getQuestionById(questionId);
        if (question == null) {
            return "redirect:/questions?error=NotFound";
        }
        System.out.println(question);
        model.addAttribute("question", question);
        model.addAttribute("users", null);
        model.addAttribute("tags", null);
        if (userService.isUserLoggedIn()) {
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUserOrNull(), UserDetailsDTO.class));
        } else {
            model.addAttribute("loggedIn", null);
        }
        model.addAttribute("answerRequestDTO", new AnswerRequestDTO());

        return "questions/detail";
    }

    @GetMapping("/ask")
    public String showCreateQuestionForm(Model model) {
        if (!userService.isUserLoggedIn()) {
            return "redirect:/users/login";
        }

        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUserOrNull(), UserDetailsDTO.class));

        return "questions/create";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("questionRequestDTO") QuestionRequestDTO questionRequestDTO,
                                 @RequestParam("tagsList") String tags) {
        System.out.println(questionRequestDTO);
        QuestionDetailsDTO createdQuestion = questionService.createQuestion(questionRequestDTO);
        return "redirect:/questions/" + createdQuestion.getId();
    }

    @GetMapping("/{id}/update")
    public String showUpdateQuestionForm(@PathVariable("id") Long questionId, Model model) {
        QuestionDetailsDTO existingQuestion = questionService.getQuestionById(questionId);
        if (existingQuestion == null) {
            return "redirect:/questions?error=NotFound";
        }

        QuestionRequestDTO questionRequestDTO = new QuestionRequestDTO();
        questionRequestDTO.setId(existingQuestion.getId());
        questionRequestDTO.setTitle(existingQuestion.getTitle());
        questionRequestDTO.setBody(existingQuestion.getBody());
        questionRequestDTO.setTagsList(
                existingQuestion.getTags().stream()
                        .map(tagDTO -> tagDTO.getName())
                        .collect(Collectors.toSet())
        );

        model.addAttribute("questionRequestDTO", questionRequestDTO);
        model.addAttribute("formAction", "/questions/update/" + questionId);  // Set the form action URL for updating
        model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));

        return "questions/create";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") Long questionId,
                                 @ModelAttribute("questionRequestDTO") QuestionRequestDTO updatedQuestionDetails,
                                 Model model) {
        questionService.updateQuestion(questionId, updatedQuestionDetails);

        updatedQuestionDetails.setUpdatedAt(LocalDateTime.now());
        String formattedTime = StackoverflowCloneApplication.formatTime(updatedQuestionDetails.getUpdatedAt());
        model.addAttribute("formattedTime", formattedTime);
        return "redirect:/questions/" + questionId;
    }


    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable("id") Long questionId) {
        Boolean isDeleted = questionService.deleteQuestion(questionId);
        if (isDeleted) {
            return "redirect:/questions?success=Deleted";
        } else {
            return "redirect:/questions?error=NotDeleted";
        }
    }

    @PostMapping("/save/{id}")
    public String saveQuestion(@PathVariable("id") Long questionId) {
        questionService.saveQuestionForUser(questionId);
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/unsave/{id}")
    public String unsaveQuestion(@PathVariable("id") Long questionId) {
        questionService.unsaveQuestionForUser(questionId);
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{questionId}/upvote")
    public String upVoteQuestion(@PathVariable("questionId") Long questionId) {
        try {
            voteService.upvoteQuestion(questionId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{questionId}/downvote")
    public String downVoteQuestion(@PathVariable("questionId") Long questionId) {
        try {
            voteService.downvoteQuestion(questionId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }

        return "redirect:/questions/" + questionId;
    }

    @GetMapping("/search")
    public String searchQuestions(@RequestParam("keyword") String keyword,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                  Model model) {


        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionDetailsDTO> questionPage = questionService.getSearchedQuestions(keyword, page,size,sort);

        model.addAttribute("questions", questionPage.getContent());
        model.addAttribute("loggedIn",userService.getLoggedInUserOrNull());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionPage.getTotalPages());
        model.addAttribute("totalElements", questionPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);

        return "dashboard";
    }


}

