package com.zakary.hospitalspringboot.mapper;

import com.zakary.hospitalspringboot.dao.*;
import com.zakary.hospitalspringboot.dao.utils.DoctorPatients;
import com.zakary.hospitalspringboot.dao.utils.PatientSickbed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
@Repository
@Mapper
public interface PatientMapper {
    int deleteByPrimaryKey(Integer patientid);
    int insert(PatientDao record);
    PatientDao selectPatientByCert(String cert_code);
    int insertSelective(PatientDao record);
    PatientDao selectByPrimaryKey(Integer patientid);
    int updateByPrimaryKeySelective(PatientDao record);
    int updateByPrimaryKey(PatientDao record);
    List<PatientDao> selectAllPatientsByDoctorCert(PageDao pageDao);
    int getAllPatientsCounts(PageDao pageDao);
    int getCountByCert(String cert_code);
    int getCountByCertInTreatment(String patient_cert_code);
    void insertPatientTreatmnet(TreatmentDao treatmentDao);
    TreatmentDao selectTreatmentByCert(String patient_cert_code);
    String isEmptyBySickbed(@Param("sickroom_id") int sickroom_id, @Param("sickbed_id") int sickbed_id);
    void updateSickbed(SickbedDao sickbedDao);
    int selectPatientInSickbedByCert(String patient_cert_code);
    List<PatientSickbed> selectPatientsSickbedInfo(PageDao pageDao);
    SickbedDao selectSickbedbyCert(String patient_cert_code);
    int selectCountInSickroom();
    int insertPrescriptionDao(PrescriptionDao prescriptionDao);
    int selectPrescriptionId(PrescriptionDao prescriptionDao);
    void insertPrescriptionAttributeDao(PrescriptionAttributeDao prescriptionAttributeDao);
    int selectPrescriptionIdByCert(@Param("doctor_cert_code")String doctor_cert_code,@Param("patient_cert_code")String patient_cert_code);
    List<Map<String,Object>> selectAllPrescriptionAttribute(PrescriptionAttributeDao prescriptionAttributeDao);
    void insertHlist(HlistDao hlistDao);
    Double selectSickrommFee(int sickroom_id);
    int hlistCountByCert(String patient_cert_code);
    HlistDao selectHlistInfoByCert(String patient_cert_code);
    List<Map<String,Object>> selectAllPatientSickbed(PageDao pageDao);
    List<Map<String,Object>> selectAllPatientNoSickbed(PageDao pageDao);
    void updatePatientHasSickbed(SickbedDao sickbedDao);
    int insertHlistInfo(PatientDao patientDao);
    void insertPatient(PatientDao patientDao);
    List<Map<String,Object>> selectAllTreatmentByPatientCertCode(TreatmentDao treatmentDao);
    void updatePatientInfoBycert(PatientDao patientDao);
    int getCountInPatientBycert(String cert_code);
    List<TreatmentDao> getCompletedTreatment(String patient_cert_code);
    List<TreatmentDao> getNotCompletedTreatment(String patient_cert_code);
    String getPatientNameByCert(String patient_cert_code);
    List<Map<String,Object>> getAllTreatmentInfo(PageDao pageDao);
    List<PrescriptionDao> selectAllPrescriptionByCert(PrescriptionDao prescriptionDao);
    List<PrescriptionAttributeDao> selectAllPrescriptionAttributeByPrescriptionId(PrescriptionAttributeDao prescriptionAttributeDao);
    int getDrugId(@Param("drug_name") String drug_name);
}