/*******************************************************************************
 *  Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *  
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *  
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *  
 *   Contributors:
 *          Kamesh Sampath - initial implementation
 *          Gregory Amerson - IDE-355
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import com.liferay.ide.eclipse.hook.core.model.internal.ServiceImplJavaTypeConstraintService;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Label( standard = "Service Wrapper" )
@Image( path = "images/elcl16/service_16x16.gif" )
public interface IService extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IService.class );

	// *** ServiceType ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.INTERFACE )
	@MustExist
	@Label( standard = "Service Type" )
	@XmlBinding( path = "service-type" )
	@Required
	ValueProperty PROP_SERVICE_TYPE = new ValueProperty( TYPE, "ServiceType" );

	ReferenceValue<JavaTypeName, JavaType> getServiceType();

	void setServiceType( String value );

	void setServiceType( JavaTypeName value );

	// *** ServiceImpl ***
	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS )
	@Label( standard = "Service Impl" )
	@XmlBinding( path = "service-impl" )
	@MustExist
	@Required
	@Service( impl = ServiceImplJavaTypeConstraintService.class )
	ValueProperty PROP_SERVICE_IMPL = new ValueProperty( TYPE, "ServiceImpl" );

	ReferenceValue<JavaTypeName, JavaType> getServiceImpl();

	void setServiceImpl( String value );

	void setServiceImpl( JavaTypeName value );

}
