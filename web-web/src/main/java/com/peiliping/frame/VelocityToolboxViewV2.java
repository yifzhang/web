package com.peiliping.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

public class VelocityToolboxViewV2 extends VelocityLayoutView {
	
	private volatile static List<Toolbox> toolboxs ;  

	@Override
	protected Context createVelocityContext(Map<String, Object> model,HttpServletRequest request, HttpServletResponse response)	throws Exception {
		
		ViewToolContext ctx = new ViewToolContext(getVelocityEngine(), request, response,getServletContext());
		ctx.putAll(model);
		if (this.getToolboxConfigLocation() != null) {
			if(toolboxs == null){
				init_tool_boxs();
			}
			if(toolboxs.size() != 0 ){
				for(Toolbox tb : toolboxs)
					ctx.addToolbox(tb);
			}
		}
		return ctx;
	}
	
	private void init_tool_boxs(){
		List<Toolbox> tmp = new ArrayList<Toolbox>();
		if (this.getToolboxConfigLocation() != null) {
			ToolManager tm = new ToolManager();
			tm.setVelocityEngine(getVelocityEngine());
			tm.configure(getServletContext().getRealPath(getToolboxConfigLocation()));
			for (String scope : Scope.values()) {
				tmp.add(tm.getToolboxFactory().createToolbox(scope));
			}
		}
		toolboxs = tmp;
	}
}
