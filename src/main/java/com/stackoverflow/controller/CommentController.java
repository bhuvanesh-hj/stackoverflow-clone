package com.stackoverflow.controller;

import com.stackoverflow.dto.CommentRequestDTO;
import com.stackoverflow.dto.QuestionDetailsDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.CommentService;
import com.stackoverflow.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/{questionId}/{answerId}/comments")
public class CommentController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final CommentService commentService;

    public CommentController(AnswerService answerService, QuestionService questionService, CommentService commentService) {
        this.answerService = answerService;
        this.questionService = questionService;
        this.commentService = commentService;
    }

    @GetMapping("/add-comment")
    public String showCommentForm(@PathVariable(required = false) Long questionId,
                                  @PathVariable(required = false) Long answerId,
                                  Model model) {
        if (questionId != null) {
            QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
            model.addAttribute("question", questionDetailsDTO);
        } else if (answerId != null) {
            Answer answerDetailsDTO = answerService.getAnswerById(answerId);
            model.addAttribute("answer", answerDetailsDTO);
        }

        model.addAttribute("comment", new CommentRequestDTO());
        return "comment/create";
    }

    @PostMapping("/saveComment")
    public String saveComment(@PathVariable(required = false) Long questionId,
                              @PathVariable(required = false) Long answerId,
                              @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            if (questionId != null) {
                QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
                model.addAttribute("question", questionDetailsDTO);
            } else if (answerId != null) {
                Answer answerDetailsDTO = answerService.getAnswerById(answerId);
                model.addAttribute("answer", answerDetailsDTO);
            }
            return "comment/create";
        }

        String formattedTime = "";

        if (questionId != null) {
             formattedTime = commentService.createComment(commentRequestDTO, questionId, null);
        } else if (answerId != null) {
             formattedTime = commentService.createComment(commentRequestDTO, null, answerId);
        }

        model.addAttribute("formattedTime", formattedTime);

        return "redirect:/questions/view/" + (questionId != null ? questionId : "answer/" + answerId);
    }

    @GetMapping("/editComment/{commentId}")
    public String editComment(@PathVariable Long commentId,
                              @PathVariable(required = false) Long questionId,
                              @PathVariable(required = false) Long answerId,
                              Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);

        if (questionId != null) {
            model.addAttribute("question", questionService.getQuestionById(questionId));
        } else if (answerId != null) {
            model.addAttribute("answer", answerService.getAnswerById(answerId));
        }

        return "comment/edit";
    }

    @PostMapping("/updateComment/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                                @PathVariable(required = false) Long questionId,
                                @PathVariable(required = false) Long answerId,
                                @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequestDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            if (questionId != null) {
                model.addAttribute("question", questionService.getQuestionById(questionId));
            } else if (answerId != null) {
                model.addAttribute("answer", answerService.getAnswerById(answerId));
            }
            return "comment/edit";
        }

        commentService.update(commentId, commentRequestDTO, questionId, answerId);

        return "redirect:/questions/view/" + (questionId != null ? questionId : "answer/" + answerId);
    }

    @PostMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                @PathVariable(required = false) Long questionId,
                                @PathVariable(required = false) Long answerId) {
        commentService.delete(commentId);
        return "redirect:/questions/view/" + (questionId != null ? questionId : "answer/" + answerId);
    }
}