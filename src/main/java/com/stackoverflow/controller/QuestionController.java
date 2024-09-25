package com.stackoverflow.controller;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.answers.AnswerRequestDTO;
import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.dto.questions.QuestionRequestDTO;
import com.stackoverflow.dto.users.UserDetailsDTO;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.exception.UserBountieException;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.*;
import com.stackoverflow.service.impl.HtmlUtils;
import com.stackoverflow.service.impl.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final AnswerService answerService;

    public QuestionController(QuestionService questionService, UserServiceImpl userService, HtmlUtils htmlUtils, ModelMapper modelMapper, VoteService voteService, CommentService commentService, CommentService commentService1, TagService tagService, AnswerService answerService) {
        this.questionService = questionService;
        this.userService = userService;
        this.htmlUtils = htmlUtils;
        this.modelMapper = modelMapper;
        this.voteService = voteService;
        this.commentService = commentService1;
        this.tagService = tagService;
        this.answerService = answerService;
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
        model.addAttribute("recentTags", tagService.getRecentTags());

        return "dashboard";
    }


    @GetMapping("/{id}")
    public String getQuestionById(@PathVariable("id") Long questionId, Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(value = "sort", defaultValue = "newest") String sort) {

        try {
            QuestionDetailsDTO question = questionService.getQuestionById(questionId);
            List<String> questionTags = question.getTags().stream().
                    map(tagDTO -> tagDTO.getName())
                    .collect(Collectors.toList());


            List<QuestionDetailsDTO> relatedQuestions = questionService.getRelatedQuestionsByTags(questionTags, questionId);
            Page<AnswerDetailsDTO> answersPage = answerService.getSearchedAnswers(page, size, sort, questionId);
            List<AnswerDetailsDTO> answers = answersPage.getContent();

            model.addAttribute("answers", answers);
            model.addAttribute("question", question);
            model.addAttribute("users", null);
            model.addAttribute("tags", null);
            model.addAttribute("relatedQuestions", relatedQuestions);
            model.addAttribute("recentTags", tagService.getRecentTags());
            model.addAttribute("loggedIn", userService.isUserLoggedIn() ? modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class) : null);
            model.addAttribute("answerRequestDTO", new AnswerRequestDTO());
            model.addAttribute("comment", new CommentRequestDTO());
            model.addAttribute("sort", sort);
            model.addAttribute("message", "Testing toast");

        } catch (ResourceNotFoundException e) {
            return "redirect:/questions?error=NotFound";
        }

        return "questions/detail";
    }

    @GetMapping("/ask")
    public String showCreateQuestionForm(Model model) {
        if (!userService.isUserLoggedIn()) {
            return "redirect:/users/login";
        }

        if (!model.containsAttribute("questionRequestDTO")) {
            model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        }

        model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUserOrNull(), UserDetailsDTO.class));

        return "questions/create";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("questionRequestDTO") QuestionRequestDTO questionRequestDTO,
                                 @RequestParam("tagsList") String tags,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            QuestionDetailsDTO createdQuestion = questionService.createQuestion(questionRequestDTO);
            return "redirect:/questions/" + createdQuestion.getId();
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("questionRequestDTO", questionRequestDTO);

            return "redirect:/questions/ask";
        }
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
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
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
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
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
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
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
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{questionId}/answer/{answerId}/accept")
    public String acceptAnswer(@PathVariable("questionId") Long questionId,
                               @PathVariable("answerId") Long answerId) {

        try {
            questionService.acceptAnswer(questionId, answerId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToAcceptAnswerException";
        }

        return "redirect:/questions/" + questionId;
    }

}

