/*
 * (c) Copyright 2008-2013 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.model.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.ListBasedSet;
import org.databene.commons.collection.NamedValueList;

/**
 * Describes a type that aggregates {@link ComponentDescriptor}s.<br/>
 * <br/>
 * Created: 03.03.2008 10:56:16
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ComplexTypeDescriptor extends TypeDescriptor implements VariableHolder {

	public static final String __SIMPLE_CONTENT = "__SIMPLE_CONTENT";

    private NamedValueList<InstanceDescriptor> parts;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public ComplexTypeDescriptor(String name, DescriptorProvider provider) {
        this(name, provider, (String) null);
    }

    public ComplexTypeDescriptor(String name, DescriptorProvider provider, ComplexTypeDescriptor parent) {
    	super(name, provider, parent);
        init();
    }
    
    public ComplexTypeDescriptor(String name, DescriptorProvider provider, String parentName) {
        super(name, provider, parentName);
        init();
    }
    
    // component handling ----------------------------------------------------------------------------------------------

    public void addPart(InstanceDescriptor part) {
    	if (part instanceof ComponentDescriptor)
    		addComponent((ComponentDescriptor) part);
    	else
    		addVariable((VariableDescriptor) part);
    }

    public void addComponent(ComponentDescriptor descriptor) {
    	String componentName = descriptor.getName();
		if (parent != null && ((ComplexTypeDescriptor) parent).getComponent(componentName) != null)
			descriptor.setParent(((ComplexTypeDescriptor) parent).getComponent(componentName));
        parts.add(componentName, descriptor);
    }

	public void setComponent(ComponentDescriptor component) {
    	String componentName = component.getName();
		if (parent != null && ((ComplexTypeDescriptor) parent).getComponent(componentName) != null)
			component.setParent(((ComplexTypeDescriptor) parent).getComponent(componentName));
        parts.set(componentName, component);
	}
   
    public ComponentDescriptor getComponent(String name) {
    	for (InstanceDescriptor part : parts.values())
    		if (StringUtil.equalsIgnoreCase(part.getName(), name) && part instanceof ComponentDescriptor)
    			return (ComponentDescriptor) part;
        if (getParent() != null)
            return ((ComplexTypeDescriptor) getParent()).getComponent(name);
        return null;
    }

    public List<InstanceDescriptor> getParts() {
        NamedValueList<InstanceDescriptor> result = NamedValueList.<InstanceDescriptor>createCaseInsensitiveList();
        
        for (InstanceDescriptor ccd : parts.values())
        	result.add(ccd.getName(), ccd);
        if (getParent() != null) {
            List<InstanceDescriptor> parentParts = ((ComplexTypeDescriptor) getParent()).getParts();
			for (InstanceDescriptor pcd : parentParts) {
                String name = pcd.getName();
				if (pcd instanceof ComponentDescriptor && !parts.containsName(name)) {
					InstanceDescriptor ccd = parts.someValueOfName(name);
	                if (ccd != null)
	                    result.add(name, ccd);
	                else
	                	result.add(name, pcd);
				}
            }
        }
        return result.values();
    }

    public List<ComponentDescriptor> getComponents() {
    	List<ComponentDescriptor> result = new ArrayList<ComponentDescriptor>();
        for (InstanceDescriptor instance : getParts())
        	if (instance instanceof ComponentDescriptor)
        		result.add((ComponentDescriptor) instance);
        return result;
    }

    public Collection<InstanceDescriptor> getDeclaredParts() {
        Set<InstanceDescriptor> declaredDescriptors = new ListBasedSet<InstanceDescriptor>(parts.size());
        for (InstanceDescriptor d : parts.values())
            declaredDescriptors.add(d);
        return declaredDescriptors;
    }

	public boolean isDeclaredComponent(String componentName) {
		return parts.containsName(componentName);
	}

    public String[] getIdComponentNames() {
    	ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
		for (ComponentDescriptor descriptor : getComponents())
			if (descriptor instanceof IdDescriptor)
				builder.add(descriptor.getName());
		return builder.toArray();
    }
	
    public List<ReferenceDescriptor> getReferenceComponents() {
    	return CollectionUtil.extractItemsOfExactType(ReferenceDescriptor.class, getComponents());
    }
	
    @Override
	public void addVariable(VariableDescriptor variable) {
        parts.add(variable.getName(), variable);
    }
    
    // construction helper methods -------------------------------------------------------------------------------------

    public ComplexTypeDescriptor withComponent(ComponentDescriptor componentDescriptor) {
        addComponent(componentDescriptor);
        return this;
    }

    @Override
    protected void init() {
    	super.init();
        this.parts = new NamedValueList<InstanceDescriptor>(NamedValueList.INSENSITIVE);
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        if (parts.size() == 0)
            return super.toString();
        //return new CompositeFormatter(false, false).render(super.toString() + '{', new CompositeAdapter(), "}");
        return getName() + getParts();
    }

}