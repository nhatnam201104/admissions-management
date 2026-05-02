package com.example.thymeleaf_web.service.impl;

import com.example.thymeleaf_web.model.dto.AspirationDto;
import com.example.thymeleaf_web.model.dto.ScoreLookupResult;
import com.example.thymeleaf_web.model.entity.DiemCong;
import com.example.thymeleaf_web.model.entity.DiemThi;
import com.example.thymeleaf_web.model.entity.Nganh;
import com.example.thymeleaf_web.model.entity.NguyenVong;
import com.example.thymeleaf_web.model.entity.Thisinh;
import com.example.thymeleaf_web.repository.DiemCongRepository;
import com.example.thymeleaf_web.repository.DiemThiRepository;
import com.example.thymeleaf_web.repository.NganhRepository;
import com.example.thymeleaf_web.repository.NguyenVongRepository;
import com.example.thymeleaf_web.repository.ThisinhRepository;
import com.example.thymeleaf_web.service.ScoreLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreLookupServiceImpl implements ScoreLookupService {

    private final ThisinhRepository thisinhRepo;
    private final DiemThiRepository diemThiRepo;
    private final DiemCongRepository diemCongRepo;
    private final NguyenVongRepository nguyenVongRepo;
    private final NganhRepository nganhRepo;

    @Override
    public Optional<ScoreLookupResult> lookupByCccd(String cccd) {
        Thisinh thisinh = thisinhRepo.findByCccdActive(cccd).orElse(null);
        if (thisinh == null) {
            return Optional.empty();
        }

        DiemThi diemThi = diemThiRepo.findByCccdActive(cccd).orElse(null);
        DiemCong diemCong = diemCongRepo.findByCccdActive(cccd).orElse(null);
        List<NguyenVong> nguyenVongs = nguyenVongRepo.findByCccdActive(cccd);

        Map<String, String> nganhNameMap = nganhRepo.findAllActive().stream()
                .collect(Collectors.toMap(Nganh::getManganh, Nganh::getTennganh, (a, b) -> a));

        List<AspirationDto> aspirationDtos = nguyenVongs.stream()
                .map(nv -> new AspirationDto(
                        nv.getNvTt(),
                        nv.getNvManganh(),
                        nganhNameMap.getOrDefault(nv.getNvManganh(), nv.getNvManganh()),
                        nv.getDiemThxt(),
                        nv.getDiemUtqd(),
                        nv.getDiemCong(),
                        nv.getDiemXettuyen(),
                        nv.getNvKetqua()
                ))
                .toList();

        String displayName = thisinh.getHoVaTen() != null
                ? thisinh.getHoVaTen()
                : thisinh.getHo() + " " + thisinh.getTen();

        return Optional.of(new ScoreLookupResult(
                thisinh.getCccd(),
                thisinh.getSobaodanh(),
                displayName,
                thisinh.getNgaySinh(),
                diemThi != null ? diemThi.getPhuongThuc() : null,
                diemThi != null ? diemThi.getToan() : null,
                diemThi != null ? diemThi.getLy() : null,
                diemThi != null ? diemThi.getHoa() : null,
                diemThi != null ? diemThi.getSinh() : null,
                diemThi != null ? diemThi.getSu() : null,
                diemThi != null ? diemThi.getDia() : null,
                diemThi != null ? diemThi.getVan() : null,
                diemThi != null ? diemThi.getN1Thi() : null,
                diemThi != null ? diemThi.getN1Cc() : null,
                diemThi != null ? diemThi.getNl1() : null,
                diemThi != null ? diemThi.getNk1() : null,
                diemThi != null ? diemThi.getNk2() : null,
                diemCong != null ? diemCong.getDiemCc() : null,
                diemCong != null ? diemCong.getDiemUtxt() : null,
                diemCong != null ? diemCong.getDiemTong() : null,
                aspirationDtos
        ));
    }

    @Override
    public List<Nganh> getAllActiveNganh() {
        return nganhRepo.findAllActive().stream()
                .sorted((a, b) -> a.getManganh().compareToIgnoreCase(b.getManganh()))
                .toList();
    }
}
