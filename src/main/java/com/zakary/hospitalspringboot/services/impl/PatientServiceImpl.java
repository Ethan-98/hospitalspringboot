package com.zakary.hospitalspringboot.services.impl;

import com.zakary.hospitalspringboot.dao.*;
import com.zakary.hospitalspringboot.dao.utils.DoctorPatients;
import com.zakary.hospitalspringboot.dao.utils.PatientSickbed;
import com.zakary.hospitalspringboot.exp.BusinessException;
import com.zakary.hospitalspringboot.mapper.DoctorMapper;
import com.zakary.hospitalspringboot.mapper.PatientMapper;
import com.zakary.hospitalspringboot.services.PatientService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    //private Logger logger = LoggerFactory.getLogger(getClass());
    Logger logger = Logger.getLogger("");
    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DoctorMapper doctotMapper;

    public int getPatientsCounts(PageDao pageDao) {
        return patientMapper.getAllPatientsCounts(pageDao);
    }

    public List<PatientDao> getAllPatientByDoctorCert(PageDao pageDao) {
        if("1".equals(pageDao.getDoctor_cert_code()))
            throw new BusinessException("id不存在");
        return patientMapper.selectAllPatientsByDoctorCert(pageDao);
    }

    public void insertPatient(TreatmentDao treatmentDao) {//此处需要在前端自动将此医生的赋值
        if(/*(treatmentDao.getDoctor_id()==null)*/
            /* (treatmentDao.getPatient_id()==null)*/
                (treatmentDao.getDoctor_cert_code()==null)
                        ||("".equals(treatmentDao.getDoctor_cert_code()))
                        ||(treatmentDao.getPatient_cert_code()==null)
                        ||("".equals(treatmentDao.getPatient_cert_code()))
                        ||(treatmentDao.getTreatment_name()==null)
                        ||("".equals(treatmentDao.getTreatment_name()))
                        ||(treatmentDao.getTreatment_time()==null)
                        ||(treatmentDao.getTreatment_fee()==null))
            throw new BusinessException("必要参数为空");
        else if(patientMapper.getCountByCert(treatmentDao.getPatient_cert_code())==0)
            throw new BusinessException("此患者不存在");
            //else if(patientMapper.getCountByCertInTreatment(treatmentDao.getPatient_cert_code())!=0)
            //throw new BusinessException("此患者已存在在治疗名单中");
        else
            patientMapper.insertPatientTreatmnet(treatmentDao);
    }

    private String isEmpty(int sickroom_id, int sickbed_id) {
        System.out.println("room:"+sickroom_id+"  bed:"+sickbed_id);
        return patientMapper.isEmptyBySickbed(sickroom_id,sickbed_id);
    }

    public void arrangeSickbed(SickbedDao sickbedDao){
        if((sickbedDao.getPatient_cert_code()==null)
                ||("".equals(sickbedDao.getPatient_cert_code()))
                ||(sickbedDao.getSickroom_id()==null)
                ||(sickbedDao.getSickbed_id()==null))
            throw new BusinessException("必要参数为空");
        else {
            System.out.println("room:"+sickbedDao.getSickroom_id()+"  bed:"+sickbedDao.getSickbed_id());
            if (!isEmpty(sickbedDao.getSickroom_id(), sickbedDao.getSickbed_id()).equals("empty")) {
                //System.out.println(patientService.isEmpty(sickbedDao.getSickroom_id(), sickbedDao.getSickbed_id()));
                throw new BusinessException("该病床已分配");
            }
            else {
                if (isInSickbed(sickbedDao.getPatient_cert_code()) != 0)
                    throw new BusinessException("该病人已存在");
                else {
                    sickbedDao.setSickbed_state("full");
                    patientMapper.updateSickbed(sickbedDao);
                    patientMapper.updatePatientHasSickbed(sickbedDao);
                }
            }
        }
//       patientMapper.insertSickbed(sickbedDao);
    }

    private int isInSickbed(String patient_cert_code){
        return patientMapper.selectPatientInSickbedByCert(patient_cert_code);
    }

    public List<PatientSickbed> getPatientsSickbedInfo(PageDao pageDao) {
        return patientMapper.selectPatientsSickbedInfo(pageDao);
    }

    @Override
    public int selectSickroomCount(){
        return patientMapper.selectCountInSickroom();
    }

    @Override
    public int insertPrescription(PrescriptionDao prescriptionDao){
        if(prescriptionDao.getDoctor_cert_code()==null
                ||prescriptionDao.getPatient_cert_code()==null
                ||"".equals(prescriptionDao.getDoctor_cert_code())
                ||"".equals(prescriptionDao.getPatient_cert_code())){
            throw new BusinessException("必要参数为空");
        }
        else {
            return patientMapper.insertPrescriptionDao(prescriptionDao);
        }
    }
    @Override
    public int getPrescriptionId(PrescriptionDao prescriptionDao){
        return patientMapper.selectPrescriptionId(prescriptionDao);
    }
    @Override
    public void insertPrescriptionAttribute(PrescriptionAttributeDao prescriptionAttributeDao){
        if(prescriptionAttributeDao.getDrug_name()==null
                ||"".equals(prescriptionAttributeDao.getDrug_name())
                ||prescriptionAttributeDao.getDrug_num()==0) {
            throw new BusinessException("必要参数为空");
        }
        else if(prescriptionAttributeDao.getDrug_num()<0){
            throw new BusinessException("输入值非法");
        }
        else {
            System.out.println(prescriptionAttributeDao.getDrug_num());
            prescriptionAttributeDao.setDrug_id(patientMapper.getDrugId(prescriptionAttributeDao.getDrug_name()));
            patientMapper.insertPrescriptionAttributeDao(prescriptionAttributeDao);
        }
    }

    private int getPrescriptionIdByCert(String doctor_cert_code,String patient_cert_code){
        return patientMapper.selectPrescriptionIdByCert(doctor_cert_code,patient_cert_code);
    }
    @Override
    public List<Map<String,Object>> getAllPrescriptionAttribute(PrescriptionAttributeDao prescriptionAttributeDao){
        List<Map<String, Object>> info;
        if(prescriptionAttributeDao.getDoctor_cert_code()==null
                ||"".equals(prescriptionAttributeDao.getDoctor_cert_code())
                ||prescriptionAttributeDao.getPatient_cert_code()==null
                ||"".equals(prescriptionAttributeDao.getPatient_cert_code())){
            throw new BusinessException("必要参数为空");
        }
        else {
            int perscription_id = getPrescriptionIdByCert(prescriptionAttributeDao.getDoctor_cert_code(), prescriptionAttributeDao.getPatient_cert_code());
            if (perscription_id == 0) {
                throw new BusinessException("此病人暂无处方");
            }
            else{
                prescriptionAttributeDao.setPrescription_id(perscription_id);
                info = patientMapper.selectAllPrescriptionAttribute(prescriptionAttributeDao);
            }
        }
        return info;
    }

    @Override
    public void setHlistByCert(HlistDao hlistDao){
        if(hlistDao.getPatient_cert_code()==null ||"".equals(hlistDao.getPatient_cert_code())){
            throw new BusinessException("必要参数为空");
        }
        if(patientMapper.hlistCountByCert(hlistDao.getPatient_cert_code())!=0){
            throw new BusinessException("此病人病历单已经生成");
        }
        PatientDao patientDao = new PatientDao();
        patientDao.setCert_code(hlistDao.getPatient_cert_code());
        patientMapper.insertHlistInfo(patientDao);
    }

    @Override
    public HlistDao getHlistByCert(String patient_cert_code){
        if(patient_cert_code==null
                ||"".equals(patient_cert_code))
            throw new BusinessException("必要参数为空");
        else
            return patientMapper.selectHlistInfoByCert(patient_cert_code);
    }

    @Override
    public void makePrescribtion(List<PrescriptionAttributeDao> prescriptionAttributeDaos,String doctor_cert_code) {
        PrescriptionDao prescriptionDao=new PrescriptionDao();
        PrescriptionAttributeDao prescriptionAttributeDao = prescriptionAttributeDaos.get(0);
        prescriptionDao.setDoctor_cert_code(doctor_cert_code);
        prescriptionDao.setPatient_cert_code(prescriptionAttributeDao.getPatient_cert_code());
        //插入病历单在prescriptiondao表中
        insertPrescription(prescriptionDao);
        int prescription_id=getPrescriptionId(prescriptionDao);
        //插入prescriptionAttribute
        //Iterator<PrescriptionAttributeDao> iter = prescriptionAttributeDaos.iterator();
        //while(iter.hasNext()){
        //    patientService.insertPrescriptionAttribute(iter.next());
        //}
//        for( int i = 0 ; i <  prescriptionAttributeDaos.size() ; i++) {
//            System.out.println( prescriptionAttributeDaos.get(i));
//            prescriptionAttributeDao.setDoctor_cert_code(doctor_cert_code);
//            prescriptionAttributeDaos.get(i).setPrescription_id(prescription_id);
//            insertPrescriptionAttribute(prescriptionAttributeDaos.get(i));
//        }
        prescriptionAttributeDaos.forEach((value)->{
            value.setDoctor_cert_code(doctor_cert_code);
            value.setPrescription_id(prescription_id);
            insertPrescriptionAttribute(value);
        });
    }

    @Override
    public List<Map<String,Object>> getAllPatientSickbed(PageDao pageDao){
        return patientMapper.selectAllPatientSickbed(pageDao);
    }

    @Override
    public List<Map<String,Object>> getAllPatientNoSickbed(PageDao pageDao){
        return patientMapper.selectAllPatientNoSickbed(pageDao);
    }

    @Override
    public void addPatient(String doctor_cert_code,PatientDao patientDao){
        patientDao.setDoctor_cert_code(doctor_cert_code);
        if(patientDao.getCert_code()==null
                &&patientDao.getDoctor_cert_code()==null
                &&patientDao.getPatient_gender()==null
                &&patientDao.getPatient_name()==null
                &&patientDao.getPatient_age()==null
                &&"".equals(patientDao.getCert_code())
                &&"".equals(patientDao.getDoctor_cert_code())
                &&"".equals(patientDao.getPatient_name())
                &&"".equals(patientDao.getPatient_gender())){
            throw new BusinessException("必要参数为空");
        }
        patientDao.setType(2);
        patientMapper.insertPatient(patientDao);
    }

    @Override
    public List<Map<String,Object>> getAllTreatmentByPatientCertCode(TreatmentDao treatmentDao){
        if(treatmentDao.getPatient_cert_code()==null
                &&"".equals(treatmentDao.getPatient_cert_code())) {
            throw new BusinessException("必要参数为空");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        patientMapper.selectAllTreatmentByPatientCertCode(treatmentDao).forEach((value)->{
            value.put("complete",value.get("complete").equals("N")?"未完成":"已完成");
            result.add(value);
        });
        return result;
    }

    @Override
    public void alterPatientInfoByCert(PatientDao patientDao){
        if(patientDao.getCert_code()==null
                &&patientDao.getPatient_name()==null
                &&patientDao.getPatient_gender()==null
                &&patientDao.getPatient_age()==null
                &&patientDao.getOld_cert_code()==null
                &&"".equals(patientDao.getCert_code())
                &&"".equals(patientDao.getPatient_name())
                &&"".equals(patientDao.getPatient_age())
                &&"".equals(patientDao.getPatient_gender())
                &&"".equals(patientDao.getOld_cert_code())){
            throw new BusinessException("必要参数为空");
        }
        if(!patientDao.getCert_code().equals(patientDao.getOld_cert_code())){
            if(patientMapper.getCountInPatientBycert(patientDao.getCert_code())!=0){
                throw new BusinessException("数据库中已存在此编号");
            }
        }
        patientMapper.updatePatientInfoBycert(patientDao);
    }

    @Override
    public List<Map<String, Object>> getTreatmentCountByCert(PageDao pageDao) {
        return patientMapper.getAllTreatmentInfo(pageDao);
    }

    @Override
    public List<PrescriptionDao> getAllPrescriptionByCert(PrescriptionDao prescriptionDao){
        if(patientMapper.getCountByCert(prescriptionDao.getPatient_cert_code())==0){
            throw new BusinessException("此患者不存在");
        }
        return patientMapper.selectAllPrescriptionByCert(prescriptionDao);
    }

    @Override
    public List<PrescriptionAttributeDao> getAllPrescriptionAttributeByPrescriptionId(PrescriptionAttributeDao prescriptionAttributeDao){
        return patientMapper.selectAllPrescriptionAttributeByPrescriptionId(prescriptionAttributeDao);
    }

//    @Override
//    public Map<String,Object> getTreatmentCountByCert(TreatmentDao treatmentDao){
//        if(treatmentDao.getPatient_cert_code()==null
//            &&"".equals(treatmentDao.getPatient_cert_code())){
//            throw new BusinessException("必要参数为空");
//        }
//        String patient_name=patientMapper.getPatientNameByCert(treatmentDao.getPatient_cert_code());
//        List<TreatmentDao> completed=patientMapper.getCompletedTreatment(treatmentDao.getPatient_cert_code());
//        List<TreatmentDao> not_completed=patientMapper.getNotCompletedTreatment(treatmentDao.getPatient_cert_code());
//        Map<String,Object> map=new HashMap<String, Object>();
//        map.put("patient_cert_code",treatmentDao.getPatient_cert_code());
//        map.put("patient_name",patient_name);
//        map.put("completed",completed.size());
//        map.put("not_complete",not_completed.size());
//        map.put("allCount",completed.size()+not_completed.size());
//        return map;
//    }
}

