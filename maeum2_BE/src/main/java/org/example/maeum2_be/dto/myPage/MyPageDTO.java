package org.example.maeum2_be.dto.myPage;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MyPageDTO {
    private List<KidInformation> kidInformationData;
    private List<GuardianInformation> guardianInformationData;
}
