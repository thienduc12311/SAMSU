package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.SamsuApiException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketUpdateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.JwtAuthenticationFilter;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventService;
import com.ftalk.samsu.service.GradeTicketService;
import com.ftalk.samsu.service.MailSenderService;
import com.ftalk.samsu.utils.AESEncryption;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.grade.GradeTicketConstants;
import com.ftalk.samsu.utils.grade.GradeTicketUtils;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GradeTicketServiceImpl implements GradeTicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeTicketServiceImpl.class);
    private static final Long expiredTime = 172800000L;

    @Autowired
    private GradeTicketRepository gradeTicketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    MailSenderService mailSenderService;
    @Autowired
    EventService eventService;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private GradeSubCriteriaRepository gradeSubCriteriaRepository;

    @Override
    public PagedResponse<GradeTicketResponse> getAllGradeTickets(int page, int size, UserPrincipal currentUser) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<GradeTicket> gradeTickets = gradeTicketRepository.findAll(pageable);
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            return getGradeTicketResponse(gradeTickets);

        }
        List<GradeTicket> content = gradeTickets.getNumberOfElements() == 0 ? Collections.emptyList() : gradeTickets.getContent();
        List<GradeTicket> filteredEvents = content
                .stream()
                .filter(gradeTicket -> Objects.equals(gradeTicket.getCreatorUser().getId(), currentUser.getId()))
                .collect(Collectors.toList());
        Page<GradeTicket> filteredPage = new PageImpl<>(filteredEvents, pageable, filteredEvents.size());
        return getGradeTicketResponse(filteredPage);


    }

    @Override
    public PagedResponse<GradeTicketResponse> getGradeTicketsByGradeSubCriteriaId(int page, int size, Integer gradeSubCriteriaId) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<GradeTicket> gradeTickets = gradeTicketRepository.findByGradeSubCriteriaId(gradeSubCriteriaId, pageable);

        List<GradeTicket> content = gradeTickets.getNumberOfElements() == 0 ? Collections.emptyList() : gradeTickets.getContent();

        return getGradeTicketResponse(gradeTickets);
    }

    @Override
    public GradeTicketResponse getGradeTicket(Integer id) {
        GradeTicket ticket = gradeTicketRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        return new GradeTicketResponse(ticket);
    }

    @Override
    public GradeTicketResponse getGradeTicketByCode(String code) {
        GradeTicket ticket = getGradeTicket(code);
        return new GradeTicketResponse(ticket);
    }

    @Transactional
    @Override
    public GradeTicketResponse updateGradeTicket(Integer id, GradeTicketUpdateRequest gradeTicketRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        Semester semester = semesterRepository.findById(gradeTicketRequest.getSemesterName()).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", gradeTicketRequest.getSemesterName()));
        GradeTicket gradeTicket = gradeTicketRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (gradeTicketRequest.getTitle() != null)
            gradeTicket.setTitle(gradeTicketRequest.getTitle());
        if (gradeTicketRequest.getSemesterName() != null)
            gradeTicket.setSemester(semester);
        if (gradeTicketRequest.getGuarantorEmail() != null) {
            if (!isAdminOrManager && gradeTicket.getGuarantorMail() != null && !gradeTicketRequest.getGuarantorEmail().equals(gradeTicket.getGuarantorMail())) {
                gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
            }
            gradeTicket.setGuarantorMail(gradeTicketRequest.getGuarantorEmail());
        }
        if (gradeTicketRequest.getContent() != null)
            gradeTicket.setContent(gradeTicketRequest.getContent());
        if (gradeTicketRequest.getEvidenceUrls() != null)
            gradeTicket.setEvidenceUrls(gradeTicketRequest.getEvidenceUrls());
        if (gradeTicketRequest.getFeedback() != null)
            gradeTicket.setFeedback(gradeTicketRequest.getFeedback());
        if (gradeTicketRequest.getGradeSubCriteriaId() != null) {
            GradeSubCriteria gradeSubCriteria = gradeSubCriteriaRepository.findById(gradeTicketRequest.getGradeSubCriteriaId()).orElseThrow(() -> new BadRequestException("GradeSubCriteria not found with id " + gradeTicketRequest.getGradeSubCriteriaId()));
            gradeTicket.setGradeSubCriteria(gradeSubCriteria);
        }
        if (isAdminOrManager) {
            if (gradeTicketRequest.getStatus() != null) {
                boolean isChangeStatus = gradeTicket.getStatus() == null || gradeTicket.getStatus() != gradeTicketRequest.getStatus();
                boolean isApproved = isChangeStatus && gradeTicketRequest.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isUnApprove = isChangeStatus && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isIntactApproved = gradeTicket.getStatus() == gradeTicketRequest.getStatus()
                        && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();

                if (isApproved) {
                    gradeTicket.setAccepterUser(user);
                    if (gradeTicketRequest.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() + gradeTicketRequest.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isUnApprove) {
                    gradeTicket.setAccepterUser(null);
                    if (gradeTicket.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() - gradeTicket.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isIntactApproved && gradeTicket.getScore() != gradeTicketRequest.getScore()) {
                    User creator = gradeTicket.getCreatorUser();
                    creator.setScore((short) (creator.getScore() - gradeTicket.getScore() + gradeTicketRequest.getScore()));
                    userRepository.save(creator);
                }
                gradeTicket.setStatus(gradeTicketRequest.getStatus());
                gradeTicket.setScore(gradeTicketRequest.getScore());

            }
        } else {
            if (gradeTicketRequest.getStatus() != null) {
                ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to change status");
                throw new UnauthorizedException(apiResponse);
            }
            gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
            gradeTicket.setAccepterUser(null);
        }
        GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
        return new GradeTicketResponse(savedGradeTicket);
    }

    @Transactional
    @Override
    public GradeTicketResponse updateGradeTicketV2(Integer id, GradeTicketUpdateRequest gradeTicketRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        GradeTicket gradeTicket = gradeTicketRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        Event event = gradeTicketRequest.getEventId() != null ? eventService.getEvent(gradeTicketRequest.getEventId(), currentUser) : null;

        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (gradeTicketRequest.getStatus() != null) {
            if (isAdminOrManager) {
                boolean isChangeStatus = gradeTicket.getStatus() == null || gradeTicket.getStatus() != gradeTicketRequest.getStatus();
                boolean isApproved = isChangeStatus && gradeTicketRequest.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isUnApprove = isChangeStatus && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isIntactApproved = gradeTicket.getStatus() == gradeTicketRequest.getStatus()
                        && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();

                if (isApproved) {
                    gradeTicket.setAccepterUser(user);
                    if (gradeTicketRequest.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() + gradeTicketRequest.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isUnApprove) {
                    gradeTicket.setAccepterUser(null);
                    if (gradeTicket.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() - gradeTicket.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isIntactApproved && gradeTicket.getScore() != gradeTicketRequest.getScore()) {
                    User creator = gradeTicket.getCreatorUser();
                    creator.setScore((short) (creator.getScore() - gradeTicket.getScore() + gradeTicketRequest.getScore()));
                    userRepository.save(creator);
                }
                gradeTicket.setStatus(gradeTicketRequest.getStatus());
                gradeTicket.setScore(gradeTicketRequest.getScore());
                if (gradeTicketRequest.getStatus() == GradeTicketConstants.APPROVED.getValue() || gradeTicketRequest.getStatus() == GradeTicketConstants.REJECTED.getValue()) {
                    gradeTicket.setAccepterUser(user);
                } else {
                    gradeTicket.setAccepterUser(null);
                }
                if (gradeTicketRequest.getFeedback() != null)
                    gradeTicket.setFeedback(gradeTicketRequest.getFeedback());
                if (gradeTicketRequest.getGradeSubCriteriaId() != null) {
                    GradeSubCriteria gradeSubCriteria = gradeSubCriteriaRepository.findById(gradeTicketRequest.getGradeSubCriteriaId()).orElseThrow(() -> new BadRequestException("GradeSubCriteria not found with id " + gradeTicketRequest.getGradeSubCriteriaId()));
                    gradeTicket.setGradeSubCriteria(gradeSubCriteria);
                }
                GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
                return new GradeTicketResponse(savedGradeTicket);
            } else {
                ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to change status");
                throw new UnauthorizedException(apiResponse);
            }
        }
        if (gradeTicketRequest.getTitle() != null)
            gradeTicket.setTitle(gradeTicketRequest.getTitle());
        if (gradeTicketRequest.getContent() != null)
            gradeTicket.setContent(gradeTicketRequest.getContent());
        if (gradeTicketRequest.getEvidenceUrls() != null)
            gradeTicket.setEvidenceUrls(gradeTicketRequest.getEvidenceUrls());
        if (gradeTicketRequest.getSemesterName() != null) {
            Semester semester = semesterRepository.findById(gradeTicketRequest.getSemesterName()).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", gradeTicketRequest.getSemesterName()));
            gradeTicket.setSemester(semester);
        }
        if (gradeTicketRequest.getGuarantorEmail() != null) {
            if (!isAdminOrManager && gradeTicket.getGuarantorMail() != null && gradeTicketRequest.getGuarantorEmail() != gradeTicket.getGuarantorMail()) {
                gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
            }
            gradeTicket.setGuarantorMail(gradeTicketRequest.getGuarantorEmail());
        }
        gradeTicket.setEventReport(event);
        gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
        gradeTicket.setAccepterUser(null);
        GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
        String code = "";
        try {
            code = AESEncryption.encrypt(gradeTicketRequest.getGuarantorEmail() + "###" + gradeTicket.getId() + "###" + System.currentTimeMillis());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SamsuApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when encrypt");
        }
        if (gradeTicketRequest.getGuarantorEmail() != null)
            mailSenderService.sendEmail(gradeTicketRequest.getGuarantorEmail(), "Access Your Grade Ticket",
                    GradeTicketUtils.genInfoSenderEmail(gradeTicketRequest.getGuarantorEmail(), code));
        return new GradeTicketResponse(savedGradeTicket);
    }

    @Override
    @Transactional
    public GradeTicketResponse addGradeTicket(GradeTicketCreateRequest gradeTicketRequest, UserPrincipal currentUser) {
//        if (AppUtils.checkEmailStaffFPT(gradeTicketRequest.getGuarantorEmail())) {
//            throw new BadRequestException("Your guarantor email is invalid!");
//        }
        if (gradeTicketRequest.getGuarantorEmail() == null && gradeTicketRequest.getEventId() == null) {
            throw new BadRequestException("Your ticket is not valid!");
        }
        User creator = userRepository.getUser(currentUser);
        Semester semester = semesterRepository.findById(gradeTicketRequest.getSemesterName()).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", gradeTicketRequest.getSemesterName()));
        Event event = gradeTicketRequest.getEventId() != null ? eventService.getEvent(gradeTicketRequest.getEventId(), currentUser) : null;
        GradeTicket gradeTicket = new GradeTicket(gradeTicketRequest.getTitle(), gradeTicketRequest.getContent(), gradeTicketRequest.getEvidenceUrls(), gradeTicketRequest.getFeedback(), creator);
        gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
        gradeTicket.setGuarantorMail(gradeTicketRequest.getGuarantorEmail());
        gradeTicket.setSemester(semester);
        gradeTicket.setEventReport(event);
        GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
        String code = "";
        try {
            code = AESEncryption.encrypt(gradeTicketRequest.getGuarantorEmail() + "###" + gradeTicket.getId() + "###" + System.currentTimeMillis());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SamsuApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception when encrypt");
        }
        if (gradeTicketRequest.getGuarantorEmail() != null)
            mailSenderService.sendEmail(gradeTicketRequest.getGuarantorEmail(), "Access Your Grade Ticket",
                    GradeTicketUtils.genInfoSenderEmail(gradeTicketRequest.getGuarantorEmail(), code));
        return new GradeTicketResponse(savedGradeTicket);
    }

    private GradeTicket getGradeTicket(String code) {
        String codeDecrypt = "";
        try {
            codeDecrypt = AESEncryption.decrypt(code);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new BadRequestException("Code is not valid");
        }

        String[] codeValue = codeDecrypt.split("###");
        if (codeValue.length != 3) {
            throw new BadRequestException("Request is not valid");
        }
        Integer id = Integer.parseInt(codeValue[1]);
        return gradeTicketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
    }

    @Override
    public ApiResponse updateGradeTicketStatusByGuarantor(String code, Short status) {
        GradeTicket gradeTicket = getGradeTicket(code);
        if (System.currentTimeMillis() - gradeTicket.getCreatedAt().getTime() > expiredTime) {
            throw new BadRequestException("This gradeTicket is expired");
        }
        if (status.equals(GradeTicketConstants.GUARANTEE_ACCEPT.getValue()) || status.equals(GradeTicketConstants.GUARANTEE_REJECT.getValue())) {
            gradeTicket.setStatus(status);
            gradeTicketRepository.save(gradeTicket);
            return new ApiResponse(Boolean.TRUE, "You successfully updated gradeTicket status");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update this gradeTicket status");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse deleteGradeTicket(Integer id, UserPrincipal currentUser) {
        GradeTicket gradeTicket = gradeTicketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
        if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            gradeTicketRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted gradeTicket");
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this gradeTicket");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public List<GradeTicket> finAllGradeTicketApproved(String semester, Integer uid) {
        return gradeTicketRepository.findAllByCreatorUser_IdAndSemester_NameAndStatus(uid, semester, GradeTicketConstants.APPROVED.getValue());
    }

    @Override
    public List<GradeTicket> finAllGradeTicketApproved(String semester) {
        return gradeTicketRepository.findAllBySemester_NameAndStatus(semester, GradeTicketConstants.APPROVED.getValue());
    }

    private PagedResponse<GradeTicketResponse> getGradeTicketResponse(Page<GradeTicket> gradeTickets) {
        if (gradeTickets.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), gradeTickets.getNumber(), gradeTickets.getSize(), gradeTickets.getTotalElements(), gradeTickets.getTotalPages(), gradeTickets.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(gradeTickets.getContent(), GradeTicketResponse::new), gradeTickets.getNumber(), gradeTickets.getSize(), gradeTickets.getTotalElements(), gradeTickets.getTotalPages(), gradeTickets.isLast());
    }
}