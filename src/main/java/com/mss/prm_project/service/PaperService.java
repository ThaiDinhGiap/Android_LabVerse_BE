package com.mss.prm_project.service;

import com.mss.prm_project.entity.Paper;

import java.util.List;

public interface PaperService {
    public List<Paper> getTop10NewestUnreadPapers(int userId);
}
