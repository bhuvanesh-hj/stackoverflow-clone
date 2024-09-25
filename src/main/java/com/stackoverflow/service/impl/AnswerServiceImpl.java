package com.stackoverflow.service.impl;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.answers.AnswerRequestDTO;
import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.VoteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private static final List<String> AI_DETECTION_KEYWORDS = Arrays.asList(
            "this is ai",
            "generated",
            "automated",
            "algorithm",
            "model",
            "machine learning",
            "deep learning",
            "artificial intelligence",
            "response generated",
            "predictive text",
            "artificial",
            "intelligence",
            "neural network",
            "robot",
            "bot",
            "chatbot",
            "language model",
            "text generation",
            "data-driven",
            "content creation",
            "optimization",
            "automation",
            "synthetic",
            "computational",
            "analysis",
            "digital assistant",
            "AI model",
            "natural language processing",
            "NLP",
            "virtual assistant"
    );

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;
    private final UserServiceImpl userService;
    private final VoteService voteService;
    private final UserRepository userRepository;


    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository, ModelMapper modelMapper, UserServiceImpl userService, VoteService voteService, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.voteService = voteService;
        this.userRepository = userRepository;
    }

    public void createAnswer(AnswerRequestDTO answerRequestDTO, Long questionId, boolean isAiGenerated) {
        User user = userService.getLoggedInUser();
        userService.isBountied(user.getId());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Answer answer = modelMapper.map(answerRequestDTO, Answer.class);

        if (isAiGenerated) {
            user.setReputations(user.getReputations() - 15);
        } else {
            user.setReputations(user.getReputations() + 5);
        }
        userRepository.save(user);

        answer.setQuestion(question);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());
        answer.setAuthor(user);
        answer.setAiGenerated(isAiGenerated);
        answer.setIsAccepted(false);
        answerRepository.save(answer);
    }

    @Override
    public AnswerDetailsDTO getAnswerById(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        AnswerDetailsDTO answerDetailsDTO = getAnswerDetailsDTO(answer);
        return answerDetailsDTO;
    }

    @Override
    public AnswerDetailsDTO updateAnswer(Long answerId, Long questionId, AnswerRequestDTO answerRequestDTO) {
        Answer existingAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        existingAnswer.setBody(answerRequestDTO.getBody());
        existingAnswer.setUpdatedAt(LocalDateTime.now());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        existingAnswer.setQuestion(question);

        Answer updatedAnswer = answerRepository.save(existingAnswer);
        AnswerDetailsDTO answerDetailsDTO = getAnswerDetailsDTO(updatedAnswer);

        return answerDetailsDTO;
    }

    @Override
    public Boolean deleteAnswer(Long answerId) {
        User user = userService.getLoggedInUser();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));
        user.setReputations(user.getReputations() - 5);
        userRepository.save(user);

        answerRepository.deleteById(answerId);
        return true;
    }

    @Override
    public List<AnswerDetailsDTO> getAnswersByUserId(Long userId) {
        return answerRepository.findByAuthorId(userId).stream()
                .map(answer -> getAnswerDetailsDTO(answer))
                .collect(Collectors.toList());
    }

    @Override
    public AnswerDetailsDTO getAnswerDetailsDTO(Answer answer) {
        AnswerDetailsDTO answerDetailsDTO = modelMapper.map(answer, AnswerDetailsDTO.class);
        boolean upvoted = false;
        boolean downvoted = false;
        int upvotes = voteService.getAnswerUpvotes(answer.getId());
        int downvotes = voteService.getAnswerDownvotes(answer.getId());

        if (userService.isUserLoggedIn()) {
            User user = userService.getLoggedInUser();
            Integer status = answerRepository.getUserVoteStatus(answer.getId(), user.getId());
            if (status != null && status == 1) {
                upvoted = true;
            } else if (status != null && status == 0) {
                downvoted = true;
            }
        }

        answerDetailsDTO.setUpvotes(upvotes);
        answerDetailsDTO.setDownvotes(downvotes);
        answerDetailsDTO.setUpvoted(upvoted);
        answerDetailsDTO.setDownvoted(downvoted);

        return answerDetailsDTO;
    }

    @Override
    public Page<AnswerDetailsDTO> getSearchedAnswers(int page, int size, String sort, Long questionId) {
        Pageable pageable;

        switch (sort.toLowerCase()) {
            case "oldest":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));  // ASC for oldest
                break;
            case "newest":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")); // DESC for newest
                break;
            case "mostliked":
                return answerRepository.findAllAnswersOrderedByUpVotes(PageRequest.of(page, size), questionId)
                        .map(answer -> modelMapper.map(answer, AnswerDetailsDTO.class));
            default:
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));  // Default to newest
        }

        Page<Answer> answers = answerRepository.findAllByQuestionId(questionId, pageable);
        return answers.map(answer -> modelMapper.map(answer, AnswerDetailsDTO.class));
    }

    @Override
    public Boolean isAiGeneratedAnswer(String answer) {
        long count = AI_DETECTION_KEYWORDS.stream()
                .filter(answer.toLowerCase()::contains)
                .count();

        return count > 7;
    }

}
