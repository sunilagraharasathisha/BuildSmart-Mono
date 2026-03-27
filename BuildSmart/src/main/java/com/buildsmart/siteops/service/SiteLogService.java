package com.buildsmart.siteops.service;

import com.buildsmart.siteops.dto.SiteLogRequest;
import com.buildsmart.siteops.dto.SiteLogResponse;
import com.buildsmart.siteops.dto.SiteLogReviewRequest;
import com.buildsmart.siteops.dto.SiteLogUpdateRequest;

import java.time.LocalDate;
import java.util.List;

public interface SiteLogService {

    SiteLogResponse createSiteLog(SiteLogRequest request);

    SiteLogResponse getSiteLogById(String logId);

    List<SiteLogResponse> getSiteLogsByProject(String projectId);

    List<SiteLogResponse> getSiteLogsByProjectAndDateRange(String projectId, LocalDate from, LocalDate to);

    SiteLogResponse getSiteLogByProjectAndDate(String projectId, LocalDate date);

    SiteLogResponse getLatestSiteLog(String projectId);

    SiteLogResponse updateSiteLog(String logId, SiteLogUpdateRequest request);

    SiteLogResponse reviewSiteLog(String logId, SiteLogReviewRequest request);
}
