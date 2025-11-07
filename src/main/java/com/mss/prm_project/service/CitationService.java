package com.mss.prm_project.service;

import com.mss.prm_project.entity.Paper;

public interface CitationService {
    String toAPA(Paper p);
    String toMLA(Paper p);
    String toBibTeX(Paper p);
}
