package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.score.AspirationScoreResult;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;

import java.util.List;

public interface AspirationScoreService {

    AspirationScoreResult calculateForAspiration(
            XtNguyenvongxettuyen aspiration);

    List<AspirationScoreResult> calculateAllForCccd(String cccd);
}
