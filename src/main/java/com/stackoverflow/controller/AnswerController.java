package com.stackoverflow.controller;

import com.stackoverflow.dto.AnswerDetailsDTO;
import com.stackoverflow.dto.AnswerRequestDTO;
import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

    @GetMapping("/add-answer{questionId}")
    public String showAnswerForm(Model model,@PathVariable Long questionId){

        QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);

        model.addAttribute("answerDTO", new AnswerDetailsDTO());
        model.addAttribute("question",questionDetailsDTO);
        return "answer/create";   // here need to change the path after adding templates
    }

    @PostMapping("/saveAnswer{questionId}")
    public String saveAnswer(
            @PathVariable Long questionId,
            @Valid @ModelAttribute("answerRequestDTO") AnswerRequestDTO answerRequestDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
            model.addAttribute("question", questionDetailsDTO);
            return "answer/create";
        }

        answerService.createAnswer(answerRequestDTO, questionId);

        String formattedTime = answerService.createAnswer(answerRequestDTO, questionId);

        model.addAttribute("formattedTime", formattedTime);


        return "redirect:/question/view" + questionId;
    }

    @GetMapping("/editAnswer{answerId}")
    public String editAnswer(@PathVariable Long answerId,Model model){
        Answer answer = answerService.getAnswerById(answerId);
        model.addAttribute("answer",answer);
        model.addAttribute("question",answer.getQuestion());
        return "answer/edit";  // here need to change the path
    }

    @PostMapping("/updateAnswer{answerId}")
    public String updateAnswer(
            @PathVariable Long answerId,
            @RequestParam Long questionId,
            @Valid @ModelAttribute("answerRequestDTO") AnswerRequestDTO answerRequestDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("question", questionService.getQuestionById(questionId));
            return "answer/edit";
        }

        answerService.update(answerId, answerRequestDTO);

        return "redirect:/question/view" + questionId;
    }


    @PostMapping("/delete{answerId}")
    public String deleteAnswer(@PathVariable Long answerId,@PathVariable Long questionId){
        answerService.delete(answerId);
        return "redirect:/question/view" + questionId;   //here need to change the controller path
    }

}