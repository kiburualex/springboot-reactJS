/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dexni.goldplan.controller;

import com.dexni.goldplan.exceptions.BadRequestException;
import com.dexni.goldplan.service.InsuranceService;
import com.dexni.goldplan.entity.Insurance;
import com.dexni.goldplan.exceptions.ResourceNotFoundException;
import com.dexni.goldplan.model.MultipleIdsMapper;
import com.dexni.goldplan.model.RequestFilterMapper;
import com.dexni.goldplan.model.RequestMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kiburu
 */
@Slf4j
@RestController
@RequestMapping("/insurances")
public class InsuranceController {
    final String path = "/insurances";
    
    @Autowired
    InsuranceService insuranceService;
    
    @GetMapping("")
    public ResponseEntity<?> listInsurances(RequestMapper requestMapper, 
            HttpServletRequest request){
        log.info("GET {} session={} request={}", path, 
                request.getSession().getId(), requestMapper.toString());
        
        Map<String, Object> responseMap = new HashMap<>();
                
        RequestFilterMapper filterMapper = requestMapper.toFilterDTO();
        List<Insurance> insurances = new ArrayList<>();
        if(filterMapper.getNoPagination().equals(1)){
            insurances = insuranceService.findAllUnpaginatedInsurances(filterMapper);
        }else{
            Page<Insurance> ctrx = insuranceService.findAllInsurances(filterMapper);
            Map<String, Object> pageProfileMap = new HashMap<>();
            if(ctrx.hasContent()){
                pageProfileMap.put("pageNumber", ctrx.getNumber());
                pageProfileMap.put("pageSize", ctrx.getSize());
                pageProfileMap.put("totalPages", ctrx.getTotalPages());
                pageProfileMap.put("totalElements", ctrx.getTotalElements());
                insurances = ctrx.getContent();
            }
            
            responseMap.put("pageProfile", pageProfileMap);
        }
        
        responseMap.put("data", insurances);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
    
    @GetMapping("/ids")
    public ResponseEntity<?> listInsurancesByIds(@RequestParam(required=true) List<Long> ids, HttpServletRequest request) {
        log.info("GET {}/ids session={} insuranceIds={}", path, request.getSession().getId(), ids);
        List<Insurance> insurances = insuranceService.findInsurancesByIdsIn(ids);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("data", insurances);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getInsuranceById(@PathVariable Long id, HttpServletRequest request) {
        log.info("GET {}/{} session={} ", path, id, request.getSession().getId());
        Insurance insurance = insuranceService.findInsuranceById(id);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("data", insurance); 
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
    
    @PostMapping("")
    public ResponseEntity createInsurance(@RequestBody Insurance insuranceRequest, HttpServletRequest request) {
        log.info("POST {} session={} payload={}", path, request.getSession().getId(), insuranceRequest.toString());
        Insurance savedInsurance = insuranceService.createInsurance(insuranceRequest);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Created successfully");
        responseMap.put("data", savedInsurance);
        return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInsurance(@PathVariable Long id, @RequestBody Insurance insuranceRequest, HttpServletRequest request) {
        log.info("PUT {}/{} session={} ", path, id, request.getSession().getId());
        Insurance savedInsurance = insuranceService.updateInsurance(id, insuranceRequest);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Updated successfully");
        responseMap.put("data", savedInsurance);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInsuranceById(@PathVariable Long id, HttpServletRequest request){
        log.info("DELETE {}/{} session={}", path, id, request.getSession().getId());
        Map<String, Object> responseMap = new HashMap<>();
        Insurance insurance = insuranceService.findInsuranceById(id);
        Boolean isDeleted = insuranceService.deleteInsuranceById(insurance.getId());
        if(!isDeleted){
            responseMap.put("message", "Insurance ID "+ id +" not deleted");
            return new ResponseEntity<>(responseMap, HttpStatus.EXPECTATION_FAILED);
        }
        
        responseMap.put("message", "Deleted successfully");
        return new ResponseEntity<>(responseMap, HttpStatus.OK); 
    }
    
    @PutMapping("/ids")
    public ResponseEntity<?> deleteInsuranceByIds(@RequestBody MultipleIdsMapper idsMapper, HttpServletRequest request){
        log.info("PUT {}/ids session={} ids={}", path, request.getSession().getId(), idsMapper.getIds());
        
        Map<String, Object> responseMap = new HashMap<>();
        Boolean isDeleted = insuranceService.deleteInsurancesByIds(idsMapper.getIds());
        if(!isDeleted){
            responseMap.put("message", "Insurance IDs ["+ idsMapper.getIds().toString() +"] not deleted");
            return new ResponseEntity<>(responseMap, HttpStatus.EXPECTATION_FAILED);
        }
        
        responseMap.put("message", "Deleted successfully");
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
        
    }
}
