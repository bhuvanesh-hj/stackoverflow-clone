package com.stackoverflow.controller;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.VoteService;
import com.stackoverflow.service.impl.HtmlUtils;
import com.stackoverflow.service.impl.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String getAllQuestions(Model model) {
        List<QuestionDetailsDTO> questions = questionService.getAllQuestions();

        model.addAttribute("questions", questions);
        model.addAttribute("HtmlUtils", htmlUtils);
        if (userService.isUserLoggedIn()) {
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));
        } else {
            model.addAttribute("loggedIn", null);
        }
        model.addAttribute("users", null);
        model.addAttribute("tags", null);

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
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));
        } else {
            model.addAttribute("loggedIn", null);
        }
        return "questions/detail";
    }

    @GetMapping("/ask")
    public String showCreateQuestionForm(Model model) {
        if (!userService.isUserLoggedIn()) {
            return "redirect:/users/login";
        }

        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));

        return "questions/create";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("questionRequestDTO") QuestionRequestDTO questionRequestDTO,
                                 @RequestParam("tagsList") String tags) {
        System.out.println(questionRequestDTO);
        QuestionDetailsDTO createdQuestion = questionService.createQuestion(questionRequestDTO);
        return "redirect:/questions/" + createdQuestion.getId();
    }

    @GetMapping("/update/{id}")
    public String showUpdateQuestionForm(@PathVariable("id") Long questionId, Model model) {
        QuestionDetailsDTO question = questionService.getQuestionById(questionId);
        if (question == null) {
            return "redirect:/questions?error=NotFound";
        }
        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        model.addAttribute("HtmlUtils", htmlUtils);
        model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));

        return "questions/update";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") Long questionId,
                                 @ModelAttribute("questionRequestDTO") QuestionRequestDTO updatedQuestionDetails,
                                 Model model) {
        QuestionDetailsDTO existingQuestion = questionService.getQuestionById(questionId);
        questionService.updateQuestion(questionId, updatedQuestionDetails);
        String formattedTime = StackoverflowCloneApplication.formatTime(existingQuestion.getUpdatedAt());
        model.addAttribute("formattedTime", formattedTime);
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long questionId) {
        Boolean isDeleted = questionService.deleteQuestion(questionId);
        if (isDeleted) {
            return "redirect:/questions?success=Deleted";
        } else {
            return "redirect:/questions?error=NotDeleted";
        }
    }

   @PostMapping("/upvote/{questionId}/{userId}")
    public String upVoteQuestion(@PathVariable("questionId") Long questionId, @PathVariable("userId") Long userId) {
        voteService.upvoteQuestion(questionId, userId);
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/downvote/{questionId}/{userId}")
    public String downVoteQuestion(@PathVariable("questionId") Long questionId, @PathVariable("userId") Long userId) {
        QuestionDetailsDTO question = questionService.vote(false, questionId, userId);
        return "redirect:/questions/" + question.getId();
    }
}

