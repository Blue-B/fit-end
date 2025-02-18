package com.example.demo.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.DTO.UserBodyInfoDTO;
import com.example.demo.Entity.ScoreRankFemale;
import com.example.demo.Entity.ScoreRankMale;
import com.example.demo.Repo.RepoScoreRankFemale;
import com.example.demo.Repo.RepoScoreRankMale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.UserBodyInfo;
import com.example.demo.Entity.UserInfo;
import com.example.demo.Repo.RepoUserBodyInfo;
import com.example.demo.Repo.RepoUserInfo;

@Service
public class UserBodyInfoService {

    @Autowired
    private RepoUserBodyInfo RepoUserBodyInfo;

    @Autowired
    private RepoUserInfo RepoUserInfo;

    @Autowired
    private RepoScoreRankMale scoreRankMaleRepository;

    @Autowired
    private RepoScoreRankFemale scoreRankFemaleRepository;

    public UserBodyInfoDTO recordeUserBodyInfo(UserBodyInfoDTO userBodyInfoDTO) {
        System.out.println(userBodyInfoDTO);

        UserBodyInfo userBodyInfo = convertToEntity(userBodyInfoDTO);

        // UserInfo 엔티티 먼저 확인 및 저장
        String userid = userBodyInfo.getUserInfo().getUserid(); // DTO에서 userid 가져오기
        UserInfo foundUserInfo = RepoUserInfo.findByUserid(userid);
        if (foundUserInfo == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
        userBodyInfo.setUserInfo(foundUserInfo);

        double fatMass = userBodyInfo.getWeight() * (userBodyInfo.getFatpercentage() / 100);
        double heightInMeters = userBodyInfo.getHeight() / 100.0;
        double bmi = userBodyInfo.getWeight() / (heightInMeters * heightInMeters);
        double inbodyScore = (100 - userBodyInfo.getFatpercentage()) + (userBodyInfo.getWeight() * 0.1);
        double leanmass = userBodyInfo.getWeight() - fatMass;
        LocalDate birth = RepoUserInfo.getUserBirthById(userid);

        userBodyInfo.setAge(calAge(birth));
        userBodyInfo.setSex(RepoUserInfo.getUserSexById(userid));
        userBodyInfo.setLeanmass(Math.round(leanmass * 100.0) / 100.0);
        userBodyInfo.setFatMass(Math.round(fatMass * 100.0) / 100.0);
        userBodyInfo.setBmi(Math.round(bmi * 100.0) / 100.0);
        userBodyInfo.setInbodyScore(Math.round(inbodyScore * 100.0) / 100.0);
        userBodyInfo.setDate(new Date());

        UserBodyInfo savedInfo = RepoUserBodyInfo.save(userBodyInfo);

        saveToScoreRank(userBodyInfo, (int) inbodyScore);

        return convertToDTO(savedInfo);
    }

    // 사용자 정보를 기반으로 점수를 저장하는 메서드입니다.
    // 성별(sex)에 따라 score_rank_male 또는 score_rank_female 테이블에 저장합니다.
    private void saveToScoreRank(UserBodyInfo userBodyInfo, int score) {

        if (userBodyInfo.getSex() == 1) { // 남성
            ScoreRankMale rankMale = scoreRankMaleRepository
                    .findByUserInfo_Userid(userBodyInfo.getUserInfo().getUserid());
            if (rankMale == null) {
                rankMale = new ScoreRankMale();
            }
            rankMale.setSex(userBodyInfo.getSex());
            rankMale.setAge(userBodyInfo.getAge());
            rankMale.setHeight(userBodyInfo.getHeight());
            rankMale.setWeight(userBodyInfo.getWeight());
            rankMale.setLeanmass(userBodyInfo.getLeanmass());
            rankMale.setFatmass(userBodyInfo.getFatMass());
            rankMale.setFatpercentage((float) userBodyInfo.getFatpercentage());
            rankMale.setScore(score);
            rankMale.setUserInfo(userBodyInfo.getUserInfo());
            scoreRankMaleRepository.save(rankMale);
        } else if (userBodyInfo.getSex() == 2) { // 여성
            ScoreRankFemale rankFemale = scoreRankFemaleRepository
                    .findByUserInfo_Userid(userBodyInfo.getUserInfo().getUserid());
            if (rankFemale == null) {
                rankFemale = new ScoreRankFemale();
            }
            rankFemale.setSex(userBodyInfo.getSex());
            rankFemale.setAge(userBodyInfo.getAge());
            rankFemale.setHeight(userBodyInfo.getHeight());
            rankFemale.setWeight(userBodyInfo.getWeight());
            rankFemale.setLeanmass(userBodyInfo.getLeanmass());
            rankFemale.setFatmass(userBodyInfo.getFatMass());
            rankFemale.setFatpercentage((float) userBodyInfo.getFatpercentage());
            rankFemale.setScore(score);
            rankFemale.setUserInfo(userBodyInfo.getUserInfo());
            scoreRankFemaleRepository.save(rankFemale);
        }
    }

    // 특정 사용자의 최근 신체 정보 기록을 가져옴
    public List<UserBodyInfo> getRecentUserBodyRecords(UserBodyInfoDTO userBodyInfoDTO) {
        return RepoUserBodyInfo.findByUserInfo_UseridOrderByDateDesc(userBodyInfoDTO.getUserid());
    }

    // 사용자의 생년월일을 기반으로 현재 연도와 비교하여 나이를 반환합니다.
    private int calAge(LocalDate birth) {
        int age = 0;

        if (birth != null) {
            LocalDate currentDate = LocalDate.now();
            age = currentDate.getYear() - birth.getYear() -
                    (currentDate.getDayOfYear() < birth.getDayOfYear() ? 1 : 0);
        }

        return age;
    }

    private UserBodyInfo convertToEntity(UserBodyInfoDTO UserBodyInfoDTO) {
        UserInfo userInfo = RepoUserInfo.findByUserid(UserBodyInfoDTO.getUserid());
        return new UserBodyInfo(
                userInfo,
                UserBodyInfoDTO.getId(),
                UserBodyInfoDTO.getHeight(),
                UserBodyInfoDTO.getWeight(),
                UserBodyInfoDTO.getFatpercentage(),
                UserBodyInfoDTO.getFatmass(),
                UserBodyInfoDTO.getLeanmass(),
                UserBodyInfoDTO.getBmi(),
                UserBodyInfoDTO.getInbodyScore(),
                UserBodyInfoDTO.getDate(),
                UserBodyInfoDTO.getSex(),
                UserBodyInfoDTO.getAge());
    }

    private UserBodyInfoDTO convertToDTO(UserBodyInfo userBodyInfo) {
        return new UserBodyInfoDTO(
                userBodyInfo.getId(),
                userBodyInfo.getUserInfo().getUserid(),
                userBodyInfo.getHeight(),
                userBodyInfo.getWeight(),
                userBodyInfo.getFatpercentage(),
                userBodyInfo.getFatMass(),
                userBodyInfo.getLeanmass(),
                userBodyInfo.getBmi(),
                userBodyInfo.getInbodyScore(),
                userBodyInfo.getDate(),
                userBodyInfo.getSex(),
                userBodyInfo.getAge());
    }
}
