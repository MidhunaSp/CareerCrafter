package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.response.JobRespDto;
import com.springboot.careercrafter.dto.response.RecommendedJobRespDto;
import com.springboot.careercrafter.exception.OwnershipException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.model.JobSeeker;
import com.springboot.careercrafter.repository.JobRepository;
import com.springboot.careercrafter.repository.JobSeekerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final JobSeekerRepository jobSeekerRepository;
    private final JobRepository jobRepository;

    public List<RecommendedJobRespDto> getRecommendedJobs(Long jobSeekerId, int limit, String loggedInUsername) {
        // step 1: fetch jobseeker, throw exception if not found
        JobSeeker js = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        // step 2: a job seeker can see recommendations only for his own account
        if (!js.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("You can see recommendations only for your own account");

        // step 3: skills "java, spring, sql" -> [java, spring, sql]
        List<String> skills = Arrays.stream(js.getSkills().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .toList();

        // step 4: score every active job
        List<RecommendedJobRespDto> result = new ArrayList<>();
        for (JobRespDto job : jobRepository.getAllActiveJobs()) {
            String text = (job.title() + " " + job.description() + " "
                    + (job.qualifications() == null ? "" : job.qualifications())).toLowerCase();

            List<String> matchedSkills = new ArrayList<>();
            for (String skill : skills) {
                if (text.contains(skill))
                    matchedSkills.add(skill);
            }

            boolean locationMatch = js.getLocation() != null
                    && js.getLocation().equalsIgnoreCase(job.location());

            // each matched skill = 10 points, same location = 20 points
            int score = matchedSkills.size() * 10 + (locationMatch ? 20 : 0);

            if (score > 0)
                result.add(new RecommendedJobRespDto(job, score, matchedSkills, locationMatch));
        }

        // step 5: highest score first, return only the top "limit"
        return result.stream()
                .sorted(Comparator.comparingInt(RecommendedJobRespDto::score).reversed())
                .limit(limit)
                .toList();
    }
}