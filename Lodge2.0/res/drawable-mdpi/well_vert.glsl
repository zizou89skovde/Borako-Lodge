#version 300 es

uniform mat4 u_MVMatrix;	 
uniform mat4 u_MVPMatrix;
uniform mat3 u_NormalMatrix;

in vec3 v_Position;
in vec3 v_Normal;						
		

out vec3 f_Position;
out vec3 f_Normal;
out vec3 f_LightDir;

void main()                                                 	
{   

	
	f_LightDir		= u_NormalMatrix*vec3(0.0,1.0,0.0);
	f_Position		= vec3(u_MVMatrix*vec4(v_Position,1.0));
	f_Normal		= u_NormalMatrix*v_Normal;

	gl_Position 	= u_MVPMatrix * vec4(v_Position,1.0);           		  	  
}                                                          
