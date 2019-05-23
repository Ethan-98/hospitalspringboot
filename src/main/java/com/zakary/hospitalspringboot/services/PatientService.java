package com.zakary.hospitalspringboot.services;

import com.zakary.hospitalspringboot.dao.*;
import com.zakary.hospitalspringboot.dao.utils.DoctorPatients;
import com.zakary.hospitalspringboot.dao.utils.PatientSickbed;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface PatientService {
    List<PatientDao> getAllPatientByDoctorCert(PageDao pageDao);
    int getPatientsCounts(PageDao pageDao);
    void insertPatient(TreatmentDao treatmentDao);
    //    String isEmpty(int sickroom_id,int sickbed_id);
    void arrangeSickbed(SickbedDao sickbedDao);
    //    int isInSickbed(String patient_cert_code);
    List<PatientSickbed> getPatientsSickbedInfo(PageDao pageDao);
    int selectSickroomCount();
    int insertPrescription(PrescriptionDao prescriptionDao);
    int getPrescriptionId(PrescriptionDao prescriptionDao);
    void insertPrescriptionAttribute(PrescriptionAttributeDao prescriptionAttributeDao);
    List<Map<String,Object>> getAllPrescriptionAttribute(PrescriptionAttributeDao prescriptionAttributeDao);
    void setHlistByCert(HlistDao hlistDao);
    HlistDao getHlistByCert(String patient_cert_dao);
    void makePrescribtion(List<PrescriptionAttributeDao> prescriptionAttributeDaos,String doctor_cert_code);
    List<Map<String,Object>> getAllPatientSickbed(PageDao pageDao);
    List<Map<String,Object>> getAllPatientNoSickbed(PageDao pageDao);
    void addPatient(String doctor_cert_code,PatientDao patientDao);
    List<Map<String,Object>> getAllTreatmentByPatientCertCode(TreatmentDao treatmentDao);
    void alterPatientInfoByCert(PatientDao patientDao);
    List<Map<String,Object>> getTreatmentCountByCert(PageDao pageDao);
    List<PrescriptionDao> getAllPrescriptionByCert(PrescriptionDao prescriptionDao);
    List<PrescriptionAttributeDao> getAllPrescriptionAttributeByPrescriptionId(PrescriptionAttributeDao prescriptionAttributeDao);
}