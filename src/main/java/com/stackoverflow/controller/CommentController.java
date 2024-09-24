package com.stackoverflow.controller;

import com.stackoverflow.dto.comments.CommentRequestDTO;
import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.exception.UserBountieException;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.CommentService;
import com.stackoverflow.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
            commentService.commentOnQuestion(commentRequestDTO, questionId);

        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
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
                              Model model) {
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

            commentService.commentOnQuestionComment(commentRequestDTO, questionId, commentId);

        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to create comment";
        }

        return "redirect:/questions/" + questionId;

    }

    @PostMapping("/{answerId}/answer")
    public String commentOnAnswer(@PathVariable(required = false) Long questionId,
                                  @PathVariable(required = false) Long answerId,
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
            commentService.commentOnAnswer(commentRequestDTO, answerId);

        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to create comment";
        }

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{answerId}/{commentId}")
    public String commentOnAnswerionComment(@PathVariable(required = false) Long questionId,
                                            @PathVariable(required = false) Long answerId,
                                            @PathVariable(required = false) Long commentId,
                                            @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                                            BindingResult bindingResult,
                                            Model model) {
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

            commentService.commentOnAnswerComment(commentRequestDTO, answerId, commentId);

        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to create comment";
        }

        return "redirect:/questions/" + questionId;

    }

    @PostMapping("/{commentId}/update")
    public String updateComment(@PathVariable Long commentId,
                                @PathVariable(required = false) Long questionId,
                                @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                                BindingResult result,
                                Model model) {

        try {
            if (result.hasErrors()) {
                if (questionId != null) {
                    model.addAttribute("question", questionService.getQuestionById(questionId));
                }
                return "redirect:/questions/" + questionId;
            }

            commentService.updateComment(commentId, commentRequestDTO, questionId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to update comment";
        }

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
                                @PathVariable(required = false) Long questionId,
                                @PathVariable(required = false) Long answerId) {
        try {
            commentService.deleteComment(commentId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (UserBountieException e) {
            return "redirect:/questions/" + questionId + "?isBountied";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=Failed to deleting comment";
        }

        return "redirect:/questions/" + questionId;
    }
}