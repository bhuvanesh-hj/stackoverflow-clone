package com.stackoverflow.controller;

import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.dto.QuestionRequestDTO;
import com.stackoverflow.dto.user.UserRegistrationDTO;
import com.stackoverflow.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public String getAllQuestions(Model model) {
        List<QuestionDetailsDTO> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        model.addAttribute("registerUser", new UserRegistrationDTO());
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

    @GetMapping("/create")
    public String showCreateQuestionForm(Model model) {
        model.addAttribute("questionRequestDTO", new QuestionRequestDTO());
        return "questions/create";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("questionRequestDTO") QuestionRequestDTO questionRequestDTO) {
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
        return "questions/update";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") Long questionId,
                                 @ModelAttribute("questionRequestDTO") QuestionRequestDTO updatedQuestionDetails) {
        questionService.updateQuestion(questionId, updatedQuestionDetails);
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

