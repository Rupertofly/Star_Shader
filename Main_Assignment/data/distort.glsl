uniform vec3 iResolution;
uniform float iGlobalTime;
uniform sampler2D ispec; //add variables for resolution time and texture

//-----------------------------

//Screen Distort by Hornet

//https://www.shadertoy.com/view/XdfGzH

//------------------------------------------------
float sawtooth( float t ) {
	return abs(mod(abs(t), 2.0)-1.0);
}

void main()
{
	vec2 uv = gl_FragCoord.xy / iResolution.xy;

    float distpow = 1.5;
    //if ( iMouse.z > 0.5 )
    //    distpow = (1.2-iMouse.x / iResolution.x) * 10.0;

	//note: domain distortion
	const vec2 ctr = vec2(0.5,0.5);
	vec2 ctrvec = ctr - uv;
	float ctrdist = length( ctrvec );
	ctrvec /= ctrdist;
	uv += ctrvec * max(0.0, pow(ctrdist, distpow)-0.0025);


	//note: lines
	// vec2 div = 40.0 * vec2(1.0, iResolution.y / iResolution.x );
	// float lines = 0.0;
// 	lines += smoothstep( 0.2, 0.0, sawtooth( uv.x*2.0*div.x ) );
// 	lines += smoothstep( 0.2, 0.0, sawtooth( uv.y*2.0*div.y ) );
// 	lines = clamp( lines, 0.0, 1.0 );

	vec3 outcol = vec3(0.0);
	outcol += texture2D(ispec, vec2(0,1)+vec2(1,-1)*uv ).rgb;
//	outcol *= vec3(1.0-lines); //black
	//outcol += vec3(lines); //white

	//note: force black outside valid range
	vec2 valid = step( vec2(0.0), uv ) * step( uv, vec2(1.0) );
	outcol *= valid.x*valid.y;

	gl_FragColor = vec4(outcol,1.0);
}
