package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.SamsuApiException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.model.achievement.AchievementTemplate;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.achievement.AchievementRequest;
import com.ftalk.samsu.payload.achievement.AchievementResponse;
import com.ftalk.samsu.payload.achievement.AchievementTemplateRequest;
import com.ftalk.samsu.payload.achievement.AchievementTemplateResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketUpdateRequest;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.*;
import com.ftalk.samsu.utils.AESEncryption;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.grade.GradeTicketConstants;
import com.ftalk.samsu.utils.grade.GradeTicketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class AchievementServiceImpl implements AchievementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AchievementServiceImpl.class);

    @Autowired
    private SemesterService semesterService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private AchievementTemplateRepository achievementTemplateRepository;

    @Override
    public PagedResponse<AchievementResponse> getAllAchievementResponse(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Achievement> achievements = achievementRepository.findAll(pageable);

        List<Achievement> content = achievements.getNumberOfElements() == 0 ? Collections.emptyList() : achievements.getContent();

        if (achievements.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), achievements.getNumber(), achievements.getSize(),
                    achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(content, AchievementResponse::new), achievements.getNumber(),
                achievements.getSize(), achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
    }

    @Override
    public List<AchievementResponse> getAchievementResponseByRollnumberAndSemester(String rollnumber, String semester) {
        User user = userRepository.getUserByRollnumber(rollnumber);
        List<Achievement> achievements = achievementRepository.findAllByOwnerIdAndSemester(user.getId(), semester);
        return ListConverter.listToList(achievements,AchievementResponse::new);
    }

    @Override
    public PagedResponse<AchievementTemplateResponse> getAllAchievementTemplateResponse(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        Page<AchievementTemplate> achievements = achievementTemplateRepository.findAll(pageable);

        List<AchievementTemplate> content = achievements.getNumberOfElements() == 0 ? Collections.emptyList() : achievements.getContent();

        if (achievements.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), achievements.getNumber(), achievements.getSize(),
                    achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(content, AchievementTemplateResponse::new), achievements.getNumber(),
                achievements.getSize(), achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
    }


    @Override
    public AchievementResponse getAchievementResponse(Integer id, UserPrincipal currentUser) {
        Achievement achievement = achievementRepository.findById(id).orElseThrow(() -> new BadRequestException("Achievement not found with id " + id));
        return new AchievementResponse(achievement);
    }

    @Override
    public AchievementTemplateResponse getAchievementTemplateResponse(Integer id, UserPrincipal currentUser) {
        AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(id).orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + id));
        return new AchievementTemplateResponse(achievementTemplate);
    }

    @Transactional
    @Override
    public AchievementResponse updateAchievementResponse(Integer id, AchievementRequest achievementRequest, UserPrincipal currentUser) {
        User creator = userRepository.getUserByRollnumber(achievementRequest.getOwnerRollnumber());
        Semester semester = semesterService.getSemester(achievementRequest.getSemesterName());
        Achievement achievement = achievementRepository.findById(id).orElseThrow(() -> new BadRequestException("Achievement not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (isAdminOrManager) {
            achievement.setTitle(achievementRequest.getTitle());
            achievement.setContent(achievementRequest.getContent());
            achievement.setOwner(creator);
            achievement.setSemester(semester);
            if (achievementRequest.getAchievementTemplateId() != null) {
                AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(achievementRequest.getAchievementTemplateId())
                        .orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + id));
                achievement.setAchievementTemplate(achievementTemplate);
            }
            achievement.setUrl(achievementRequest.getUrl());
        }
        return new AchievementResponse(achievementRepository.save(achievement));
    }

    @Transactional
    @Override
    public AchievementTemplateResponse updateAchievementTemplateResponse(Integer id, AchievementTemplateRequest achievementTemplateRequest,
                                                                         UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(id).orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (isAdminOrManager) {
            achievementTemplate.setTitle(achievementTemplateRequest.getTitle());
            achievementTemplate.setContent(achievementTemplateRequest.getContent());
        }
        return new AchievementTemplateResponse(achievementTemplateRepository.save(achievementTemplate));
    }


    @Transactional
    @Override
    public AchievementResponse createAchievementResponse(AchievementRequest achievementRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        User owner = userRepository.getUserByRollnumber(achievementRequest.getOwnerRollnumber());
        Semester semester = semesterService.getSemester(achievementRequest.getSemesterName());
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            throw new UnauthorizedException("You don't have permission");
        }
        Achievement achievement = new Achievement(achievementRequest.getTitle(), achievementRequest.getContent(), achievementRequest.getUrl());
        achievement.setSemester(semester);
        achievement.setOwner(owner);
        achievement.setCreator(user);
        if (achievementRequest.getAchievementTemplateId() != null) {
            AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(achievementRequest.getAchievementTemplateId())
                    .orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + achievementRequest.getAchievementTemplateId()));
            achievement.setAchievementTemplate(achievementTemplate);
        }

        return new AchievementResponse(achievementRepository.save(achievement));
    }

    @Transactional
    @Override
    public AchievementTemplateResponse createAchievementTemplateResponse(AchievementTemplateRequest achievementTemplateRequest, UserPrincipal currentUser) {
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            throw new UnauthorizedException("You don't have permission");
        }
        AchievementTemplate achievement = new AchievementTemplate(achievementTemplateRequest.getTitle(), achievementTemplateRequest.getContent());
        return new AchievementTemplateResponse(achievementTemplateRepository.save(achievement));
    }

}