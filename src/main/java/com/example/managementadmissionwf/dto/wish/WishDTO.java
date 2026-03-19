package com.example.managementadmissionwf.dto.wish;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishDTO {

    private Integer id;

    private String nnCccd;      // CCCD
    private String nvManganh;   // Mã ngành
    private Integer nvTt;       // Thứ tự NV

    private Double diemThxt;    // Điểm tổ hợp
    private Double diemUtqd;    // Ưu tiên
    private Double diemCong;    // Điểm cộng
    private Double diemXettuyen;// Tổng

    private String nvKetqua;    // TRUNG_TUYEN / TRUOT / CHO_XET

    // JOIN DATA
    private String tenNganh;
    private String hoTenThiSinh;
    
	public WishDTO(int i, String string, String string2, String string3, String string4, int j, double d, double e,
			double f, double g, String string5) {
		// TODO Auto-generated constructor stub
	}
	public WishDTO() {
		// TODO Auto-generated constructor stub
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNnCccd() {
		return nnCccd;
	}
	public void setNnCccd(String nnCccd) {
		this.nnCccd = nnCccd;
	}
	public String getNvManganh() {
		return nvManganh;
	}
	public void setNvManganh(String nvManganh) {
		this.nvManganh = nvManganh;
	}
	public Integer getNvTt() {
		return nvTt;
	}
	public void setNvTt(Integer nvTt) {
		this.nvTt = nvTt;
	}
	public Double getDiemThxt() {
		return diemThxt;
	}
	public void setDiemThxt(Double diemThxt) {
		this.diemThxt = diemThxt;
	}
	public Double getDiemUtqd() {
		return diemUtqd;
	}
	public void setDiemUtqd(Double diemUtqd) {
		this.diemUtqd = diemUtqd;
	}
	public Double getDiemCong() {
		return diemCong;
	}
	public void setDiemCong(Double diemCong) {
		this.diemCong = diemCong;
	}
	public Double getDiemXettuyen() {
		return diemXettuyen;
	}
	public void setDiemXettuyen(Double diemXettuyen) {
		this.diemXettuyen = diemXettuyen;
	}
	public String getNvKetqua() {
		return nvKetqua;
	}
	public void setNvKetqua(String nvKetqua) {
		this.nvKetqua = nvKetqua;
	}
	public String getTenNganh() {
		return tenNganh;
	}
	public void setTenNganh(String tenNganh) {
		this.tenNganh = tenNganh;
	}
	public String getHoTenThiSinh() {
		return hoTenThiSinh;
	}
	public void setHoTenThiSinh(String hoTenThiSinh) {
		this.hoTenThiSinh = hoTenThiSinh;
	}
    
    
}