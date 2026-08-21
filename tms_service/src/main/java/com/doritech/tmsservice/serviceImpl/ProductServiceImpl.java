package com.doritech.tmsservice.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Override
	public ResponseEntity createProduct(List<ProductRequest> productRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity getProductById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity getAllProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity deleteProductDetails() {
		// TODO Auto-generated method stub
		return null;
	}

}
