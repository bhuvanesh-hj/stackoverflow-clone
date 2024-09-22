package com.stackoverflow.controller;

import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.CommentService;
import com.stackoverflow.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/questions/{questionId}/comments")
public class CommentController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final CommentService commentService;

    public CommentController(AnswerService answerService, QuestionService questionService, CommentService commentService) {
        this.answerService = answerService;
        this.questionService = questionService;
        this.commentService = commentService;
    }

    @PostMapping()
    public String saveComment(@PathVariable(required = false) Long questionId,
                              @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                              BindingResult result,
                              Model model) {
        try {
            if (result.hasErrors()) {
                if (questionId != null) {
                    QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
                    model.addAttribute("question", questionDetailsDTO);
                }
                return "redirect:/questions/" + questionId;
            }

            String formattedTime = "";

            if (questionId != null) {
                formattedTime = commentService.createComment(commentRequestDTO, questionId);
            }

            model.addAttribute("formattedTime", formattedTime);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to create comment";
        }


        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{commentId}")
    public String saveComment(@PathVariable(required = false) Long questionId,
                              @PathVariable(required = false) Long commentId,
                              @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                              BindingResult bindingResult,
                              Model model){
        List<String> errorsList = new ArrayList<>();
        try {
            if (bindingResult.hasErrors()) {
                errorsList = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                model.addAttribute("errors_comment", errorsList);
                if (questionId != null) {
                    QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
                    model.addAttribute("question", questionDetailsDTO);
                }
                return "redirect:/questions/" + questionId;
            }
            String formattedTime = "";

            if (questionId != null) {
                formattedTime = commentService.createComment(commentRequestDTO, questionId, commentId);
            }

            model.addAttribute("formattedTime", formattedTime);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to create comment";
        }

        return "redirect:/questions/" + questionId;

    }

    @GetMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long commentId,
                              @PathVariable(required = false) Long questionId,
                              Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);

        if (questionId != null) {
            model.addAttribute("question", questionService.getQuestionById(questionId));
        }
        return "comment/edit";
    }

    @PostMapping("/{commentId}/edit")
    public String updateComment(@PathVariable Long commentId,
                                @PathVariable(required = false) Long questionId,
                                @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            if (questionId != null) {
                model.addAttribute("question", questionService.getQuestionById(questionId));
            }
            return "redirect:/questions/" + questionId;
        }

        commentService.update(commentId, commentRequestDTO, questionId);

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
                                @PathVariable(required = false) Long questionId,
                                @PathVariable(required = false) Long answerId) {
        commentService.delete(commentId);
        return "redirect:/questions/" + questionId;
    }
}