package com.stackoverflow.controller;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/{questionId}/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;


    @Autowired
    public AnswerController(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @GetMapping("/add-answer")
    public String showAnswerForm(Model model,@PathVariable Long questionId){

        QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);

        model.addAttribute("answerDTO", new AnswerDetailsDTO());
        model.addAttribute("question",questionDetailsDTO);
        return "answer/create";
    }

    @PostMapping("/saveAnswer")
    public String saveAnswer(
            @PathVariable Long questionId,
            @Valid @ModelAttribute("answerRequestDTO") AnswerRequestDTO answerRequestDTO,
            BindingResult result,
            Model model) {

        List<String> errorsList = new ArrayList<>();

        if (result.hasErrors()) {
            errorsList = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors_register", errorsList);

            QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
            model.addAttribute("question", questionDetailsDTO);

            return "answer/create";
        }

        try {

            String formattedTime = answerService.createAnswer(answerRequestDTO, questionId);
            model.addAttribute("formattedTime", formattedTime);

        } catch (Exception e) {
            errorsList.add(e.getMessage());
            model.addAttribute("errors_register", errorsList);

            QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
            model.addAttribute("question", questionDetailsDTO);
            return "answer/create";
        }
        return "redirect:/question/view/" + questionId;
    }

    @GetMapping("/editAnswer{answerId}")
    public String editAnswer(@PathVariable Long answerId,Model model){
        Answer answer = answerService.getAnswerById(answerId);
        model.addAttribute("answer",answer);
        model.addAttribute("question",answer.getQuestion());
        return "answer/edit";
    }

    @PostMapping("/updateAnswer/{answerId}")
    public String updateAnswer(
            @PathVariable Long answerId,
            @RequestParam Long questionId,
            @Valid @ModelAttribute("answerRequestDTO") AnswerRequestDTO answerRequestDTO,
            BindingResult result,
            Model model) {

        List<String> errorsList = new ArrayList<>();

        if (result.hasErrors()) {
            errorsList = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("error_update", errorsList);

            Answer existingAnswer = answerService.getAnswerById(answerId);
            model.addAttribute("answer", existingAnswer);

            return "answer/edit";
        }

        try {
            answerService.update(answerId, questionId, answerRequestDTO);

            Answer updatedAnswer = answerService.getAnswerById(answerId);
            String formattedTime = StackoverflowCloneApplication.formatTime(updatedAnswer.getUpdatedAt());
            model.addAttribute("formattedTime", formattedTime);

        } catch (ResourceNotFoundException e) {
            errorsList.add(e.getMessage());
            model.addAttribute("error_update", errorsList);

            Answer existingAnswer = answerService.getAnswerById(answerId);
            model.addAttribute("answer", existingAnswer);

            return "answer/edit";
        }

        return "redirect:/question/view/" + questionId;
    }

    @PostMapping("/delete{answerId}")
    public String deleteAnswer(@PathVariable Long answerId,@PathVariable Long questionId){
        answerService.delete(answerId);
        return "redirect:/question/view" + questionId;
    }

}