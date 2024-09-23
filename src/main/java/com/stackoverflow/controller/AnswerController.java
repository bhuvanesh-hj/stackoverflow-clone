package com.stackoverflow.controller;

import com.stackoverflow.StackoverflowCloneApplication;
import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.answers.AnswerRequestDTO;
import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.dto.users.UserDetailsDTO;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.exception.UserNotAuthenticatedException;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.UserService;
import com.stackoverflow.service.VoteService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final VoteService voteService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public AnswerController(AnswerService answerService, QuestionService questionService,
                            VoteService voteService, UserService userService,
                            ModelMapper modelMapper) {
        this.answerService = answerService;
        this.questionService = questionService;
        this.voteService = voteService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/saveAnswer")
    public String saveAnswer(
            @PathVariable Long questionId,
            @Valid @ModelAttribute("answerRequestDTO") AnswerRequestDTO answerRequestDTO,
            BindingResult result,
            Model model) {

        List<String> errorsList = new ArrayList<>();

        if (!userService.isUserLoggedIn()) {
            return "redirect:/users/login";
        }

        if (result.hasErrors()) {
            errorsList = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors_answers", errorsList);

            QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
            model.addAttribute("question", questionDetailsDTO);

            return "redirect:/questions/" + questionId;
        }

        try {
            AnswerDetailsDTO answerDetailsDTO = answerService.createAnswer(answerRequestDTO, questionId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            errorsList.add(e.getMessage());
            model.addAttribute("errors_answers", errorsList);
            QuestionDetailsDTO questionDetailsDTO = questionService.getQuestionById(questionId);
            model.addAttribute("question", questionDetailsDTO);

            return "redirect:/questions/" + questionId;
        }

        return "redirect:/questions/" + questionId;
    }

    @GetMapping("/{answerId}/editAnswer")
    public String editAnswer(@PathVariable Long answerId,
                             @PathVariable("questionId") Long questionId,
                             Model model) {
        QuestionDetailsDTO question = questionService.getQuestionById(questionId);
        AnswerDetailsDTO answerDetailsDTO = answerService.getAnswerById(answerId);
        List<String> questionTags = question.getTags().stream().
                map(tagDTO -> tagDTO.getName())
                .collect(Collectors.toList());
        List<QuestionDetailsDTO> relatedQuestions = questionService.getRelatedQuestionsByTags(questionTags, questionId);

        if (!userService.isUserLoggedIn()) {
            return "redirect:/users/login";
        } else if (question == null) {
            throw new ResourceNotFoundException("Question not found");
        } else if (answerDetailsDTO == null) {
            throw new ResourceNotFoundException("Answer not found");
        } else if (!answerDetailsDTO.getAuthor().getUsername().equals(userService.getLoggedInUser().getUsername())) {
            throw new UserNotAuthenticatedException("You are not authorized to edit this answer");
        }

        model.addAttribute("question", question);
        model.addAttribute("users", null);
        model.addAttribute("tags", null);
        model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));
        model.addAttribute("updatingAnswer", answerDetailsDTO);
        model.addAttribute("relatedQuestions", relatedQuestions);


        return "questions/detail";
    }

    @PostMapping("/{answerId}/updateAnswer")
    public String updateAnswer(
            @PathVariable Long answerId,
            @PathVariable Long questionId,
            @Valid @ModelAttribute("answerRequestDTO") AnswerRequestDTO answerRequestDTO,
            BindingResult result,
            Model model) {

        if (!userService.isUserLoggedIn()) {
            return "redirect:/users/login";
        }

        QuestionDetailsDTO question = questionService.getQuestionById(questionId);
        List<String> errorsList = new ArrayList<>();

        if (result.hasErrors()) {
            errorsList = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors_answers", errorsList);

            AnswerDetailsDTO existingAnswer = answerService.getAnswerById(answerId);
            model.addAttribute("question", question);
            model.addAttribute("users", null);
            model.addAttribute("tags", null);
            model.addAttribute("loggedIn", modelMapper.map(userService.getLoggedInUser(), UserDetailsDTO.class));
            model.addAttribute("updatingAnswer", existingAnswer);

            return "questions/detail";
        }

        try {
            answerService.updateAnswer(answerId, questionId, answerRequestDTO);

            AnswerDetailsDTO updatedAnswer = answerService.getAnswerById(answerId);
            String formattedTime = StackoverflowCloneApplication.formatTime(updatedAnswer.getUpdatedAt());
            model.addAttribute("formattedTime", formattedTime);

        } catch (ResourceNotFoundException e) {
            errorsList.add(e.getMessage());
            model.addAttribute("errors_answers", errorsList);

            AnswerDetailsDTO existingAnswer = answerService.getAnswerById(answerId);
            model.addAttribute("answer", existingAnswer);

            return "questions/detail";
        }

        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{answerId}/deleteAnswer")
    public String deleteAnswer(@PathVariable Long answerId, @PathVariable Long questionId) {
        answerService.deleteAnswer(answerId);
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{answerId}/upvote")
    public String upvoteAnswer(@PathVariable("answerId") Long answerId,
                               @PathVariable("questionId") Long questionId) {
        try {
            voteService.upvoteAnswer(answerId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{answerId}/downvote")
    public String downvoteAnswer(@PathVariable("answerId") Long answerId,
                                 @PathVariable("questionId") Long questionId) {
        try {
            voteService.downvoteAnswer(answerId);
        } catch (UserNotAuthenticatedException e) {
            return "redirect:/users/login";
        } catch (Exception e) {
            return "redirect:/questions/" + questionId + "?error=FailedToVote";
        }
        return "redirect:/questions/" + questionId;
    }

}