package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.model.achievement.AchievementTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementTemplateRepository extends JpaRepository<AchievementTemplate, Integer> {
}
