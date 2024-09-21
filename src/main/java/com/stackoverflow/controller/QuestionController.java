package com.stackoverflow.controller;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.*;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.entity.Tag;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.CommentService;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.TagService;
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
    private final CommentService commentService;
    private final TagService tagService;

    public QuestionController(QuestionService questionService, UserServiceImpl userService, HtmlUtils htmlUtils, ModelMapper modelMapper, VoteService voteService, CommentService commentService, CommentService commentService1, TagService tagService) {
        this.questionService = questionService;
        this.userService = userService;
        this.htmlUtils = htmlUtils;
        this.modelMapper = modelMapper;
        this.voteService = voteService;
        this.commentService = commentService1;
        this.tagService = tagService;
    }

    @GetMapping
    public String getAllQuestions(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                  @RequestParam(value = "sort", defaultValue = "newest") String sort,
                                  Model model) {
        Page<QuestionDetailsDTO> questionsPage;

        if (keyword != null && !keyword.isEmpty()) {
            questionsPage = questionService.getSearchedQuestions(keyword, page, size, sort);
            model.addAttribute("keyword", keyword);
        } else {
            questionsPage = questionService.getAllQuestions(page, size, sort);
        }

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
        model.addAttribute("question", question);
        model.addAttribute("users", null);
        model.addAttribute("tags", null);
        if (userService.isUserLoggedIn()) {
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUserOrNull(), UserDetailsDTO.class));
        } else {
            model.addAttribute("loggedIn", null);
        }
        model.addAttribute("answerRequestDTO", new AnswerRequestDTO());
        model.addAttribute("comment", new CommentRequestDTO());

        List<String> questionTags = question.getTags().stream().map(tagDTO -> tagDTO.getName()).collect(Collectors.toList());
        List<QuestionDetailsDTO> relatedQuestions = questionService.getRelatedQuestionsByTags(questionTags, questionId);

        model.addAttribute("relatedQuestions", relatedQuestions);

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

    @PostMapping("/{id}/save")
    public String saveQuestion(@PathVariable("id") Long questionId) {
        try {
            questionService.saveQuestionForUser(questionId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{id}/unsave")
    public String unsaveQuestion(@PathVariable("id") Long questionId) {
        try {
            questionService.unsaveQuestionForUser(questionId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }

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

}

