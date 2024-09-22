package com.stackoverflow.service.impl;

import com.stackoverflow.dto.answers.AnswerDetailsDTO;
import com.stackoverflow.dto.questions.QuestionDetailsDTO;
import com.stackoverflow.dto.questions.QuestionRequestDTO;
import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.QuestionVote;
import com.stackoverflow.entity.Tag;
import com.stackoverflow.entity.User;
import com.stackoverflow.exception.ResourceNotFoundException;
import com.stackoverflow.repository.AnswerRepository;
import com.stackoverflow.repository.QuestionRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.VoteService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final UserServiceImpl userService;
    private final VoteService voteService;
    private final UserRepository userRepository;
    private final AnswerService answerService;

    public QuestionServiceImpl(QuestionRepository questionRepository, TagRepository tagRepository, AnswerRepository answerRepository,
                               ModelMapper modelMapper, UserServiceImpl userService, VoteService voteService, UserRepository userRepository, AnswerService answerService) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.voteService = voteService;
        this.userRepository = userRepository;
        this.answerService = answerService;
    }

    @Override
    public Page<QuestionDetailsDTO> getAllQuestions(int page, int size, String sort) {

        Pageable pageable;
        switch (sort.toLowerCase()) {
            case "oldest":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
                break;
            case "newest":
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
                break;
            default:
                return questionRepository.findAllQuestionsOrderedByUpvotes(PageRequest.of(page, size))
                        .map(this::getQuestionDetailsDTO);
        }

        return questionRepository.findAll(pageable)
                .map(this::getQuestionDetailsDTO);
    }

    @Override
    public QuestionDetailsDTO getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        return getQuestionDetailsDTO(question);
    }

    @Override
    @Transactional
    public QuestionDetailsDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        User user = userService.getLoggedInUser();

        Question question = modelMapper.map(questionRequestDTO, Question.class);
        question.setAuthor(user);

        Set<Tag> tags = questionRequestDTO.getTagsList().stream()
                .map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> new Tag(tagName)))
                .collect(Collectors.toSet());

        question.setTags(tags);

        Question updatedQuestion = questionRepository.save(question);
        return getQuestionDetailsDTO(updatedQuestion);
    }

    public QuestionDetailsDTO updateQuestion(Long questionId, QuestionRequestDTO updatedUserDetails) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existingQuestion.setTitle(updatedUserDetails.getTitle());
        existingQuestion.setBody(updatedUserDetails.getBody());
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        Set<Tag> updatedTags = new HashSet<>();
        for (String tagName : updatedUserDetails.getTagsList()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            updatedTags.add(tag);
        }

        existingQuestion.setTags(updatedTags);

        Question updatedQuestion = questionRepository.save(existingQuestion);

        return getQuestionDetailsDTO(updatedQuestion);
    }

    @Override
    public Boolean deleteQuestion(Long questionId) {
        User user = userService.getLoggedInUser();
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        questionRepository.deleteById(questionId);
        return true;
    }

    @Override
    public QuestionDetailsDTO vote(Boolean isUpvote, Long questionId, Long userId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        QuestionVote upvote = new QuestionVote(isUpvote, question, user);
        question.getQuestionVotes().add(upvote);
        Question updateQuestion = questionRepository.save(question);

        return getQuestionDetailsDTO(question);
    }

    @Transactional
    public void saveQuestionForUser(Long questionId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        user.getQuestionsSaved().add(question);
        question.getSavedByUsers().add(user);

        userRepository.save(user);
    }

    @Override
    public void unsaveQuestionForUser(Long questionId) {
        User user = userService.getLoggedInUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        user.getQuestionsSaved().remove(question);
        question.getSavedByUsers().remove(user);

        userRepository.save(user);
    }

    @Override
    public List<QuestionDetailsDTO> getQuestionsByUser(Long userId) {
        return questionRepository.findByAuthorId(userId).stream()
                .map(question -> getQuestionDetailsDTO(question))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDetailsDTO> getSavedQuestionsByUser(Long userId) {
        return questionRepository.findBySavedByUsers_Id(userId).stream()
                .map(question -> getQuestionDetailsDTO(question))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionDetailsDTO> getAnsweredQuestions(Long id) {
        return questionRepository.findByAnswers_AuthorId(id).stream()
                .map(question -> getQuestionDetailsDTO(question))
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuestionDetailsDTO> getSearchedQuestions(String keyword, int page, int size, String sort) {
        // Fetch paginated Question entities
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "updatedAt"));
        return questionRepository.getSearchQuestions(keyword, pageable)
                .map(question -> getQuestionDetailsDTO(question));
    }


    @Transactional
    public QuestionDetailsDTO getQuestionDetailsDTO(Question question) {
        int upvotes = voteService.getQuestionUpvotes(question.getId());
        int downvotes = voteService.getQuestionDownvotes(question.getId());
        boolean upvoted = false;
        boolean downvoted = false;

        QuestionDetailsDTO questionDetailsDTO = modelMapper.map(question, QuestionDetailsDTO.class);

        Set<AnswerDetailsDTO> answerDetailsDTOS = question.getAnswers().stream()
                .map(answer -> answerService.getAnswerDetailsDTO(answer)).collect(Collectors.toSet());

        if (userService.isUserLoggedIn()) {
            User user = userService.getLoggedInUser();
            questionDetailsDTO.setIsSaved(question.getSavedByUsers().contains(user));

            Integer status = questionRepository.getUserVoteStatus(question.getId(), user.getId());
            if (status != null && status == 1) {
                upvoted = true;
            } else if (status != null && status == 0) {
                downvoted = true;
            }
        }

        questionDetailsDTO.setAnswers(answerDetailsDTOS);
        questionDetailsDTO.setAnswersCount(answerRepository.countByQuestionId(questionDetailsDTO.getId()));
        questionDetailsDTO.setUpvotes(upvotes);
        questionDetailsDTO.setDownvotes(downvotes);
        questionDetailsDTO.setUpvoted(upvoted);
        questionDetailsDTO.setDownvoted(downvoted);

        return questionDetailsDTO;
    }

    public List<QuestionDetailsDTO> getRelatedQuestionsByTags(List<String> tags, Long questionId) {
        Pageable limit = PageRequest.of(0, 3);

        List<Question> relatedQuestions = questionRepository.findRelatedQuestionsByTags(tags, questionId, limit);

        return relatedQuestions.stream()
                .map(question -> modelMapper.map(question, QuestionDetailsDTO.class))
                .collect(Collectors.toList());
    }
}
