package com.stackoverflow.controller;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.dto.user.UserDetailsDTO;
import com.stackoverflow.entity.Question;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final UserServiceImpl userService;
    private final HtmlUtils htmlUtils;

    public QuestionController(QuestionService questionService, UserServiceImpl userService, HtmlUtils htmlUtils) {
        this.questionService = questionService;
        this.userService = userService;
        this.htmlUtils = htmlUtils;
    }

    @GetMapping
    public String getAllQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();

        model.addAttribute("questions", questions);
        model.addAttribute("HtmlUtils", htmlUtils);
        model.addAttribute("loggedIn", userService.getLoggedInUserDetails());

        return "dashboard";
    }

    @GetMapping("/{id}")
    public String getQuestionById(@PathVariable("id") Long questionId, Model model) {
        QuestionDetailsDTO question = questionService.getQuestionById(questionId);
        if (question == null) {
            return "redirect:/questions?error=NotFound";
        }
        model.addAttribute("question", question);
        return "questions/detail";
    }

    @GetMapping("/ask")
    public String showCreateQuestionForm(Model model) {
        UserDetailsDTO dto = userService.getLoggedInUserDetails();

        if (dto == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        model.addAttribute("loggedIn", dto);

        return "questions/create";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("questionRequestDTO") QuestionRequestDTO questionRequestDTO,
                                 @RequestParam("tagsList") String tags) {
        QuestionDetailsDTO createdQuestion = questionService.createQuestion(questionRequestDTO, tags);
//        return "redirect:/questions/" + createdQuestion.getId();
        return "redirect:/questions";
    }

    @GetMapping("/update/{id}")
    public String showUpdateQuestionForm(@PathVariable("id") Long questionId, Model model) {
        QuestionDetailsDTO question = questionService.getQuestionById(questionId);
        if (question == null) {
            return "redirect:/questions?error=NotFound";
        }
        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        model.addAttribute("HtmlUtils", htmlUtils);
        return "questions/update";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") Long questionId,
                                 @ModelAttribute("questionRequestDTO") QuestionRequestDTO updatedQuestionDetails,
                                 Model model) {
        QuestionDetailsDTO existingQuestion = questionService.getQuestionById(questionId);
        questionService.updateQuestion(questionId, updatedQuestionDetails);
        String formattedTime = StackoverflowCloneApplication.formatTime(existingQuestion.getUpdatedAt());
        model.addAttribute("formattedTime",formattedTime);
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
}

