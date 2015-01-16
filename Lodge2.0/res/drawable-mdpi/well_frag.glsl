#version 300 es
precision mediump float;        


out vec4 out_Color;
in vec3 f_Position;
in vec3 f_Normal;
in vec3 f_LightDir;

void main(void)
{
	vec4 color = vec4(0.5,0.7,0.3,1.0);
    
	vec3 l = normalize(f_LightDir);
	vec3 e = normalize(-f_Position);
	vec3 n = normalize(f_Normal);
	vec3 r = reflect(-l,n);
	
	float specular = max(dot(r,e),0.0);
	specular = pow(specular,20.0);
	
	float diffuse = max(dot(n,l),0.0);
	float ambient = 0.1;

	out_Color = (specular+diffuse+ambient)*color;
}


