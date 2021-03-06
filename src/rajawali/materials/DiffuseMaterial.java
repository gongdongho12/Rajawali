package rajawali.materials;

import rajawali.lights.ALight;

import com.monyetmabuk.livewallpapers.photosdof.R;

public class DiffuseMaterial extends AAdvancedMaterial {
	
	public DiffuseMaterial() {
		this(R.raw.diffuse_material_vertex, R.raw.diffuse_material_fragment);
	}
	
	public DiffuseMaterial(int vertex_resID, int fragment_resID)
	{
		super(vertex_resID, fragment_resID);
	}
	
	public DiffuseMaterial(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
	}
	
	public void setShaders(String vertexShader, String fragmentShader) {
		StringBuffer fc = new StringBuffer();
		StringBuffer vc = new StringBuffer();

		for(int i=0; i<mLights.size(); ++i) {
			ALight light = mLights.get(i);
			
			if(light.getLightType() == ALight.POINT_LIGHT) {
				vc.append("dist = distance(V.xyz, uLightPosition").append(i).append(");\n");
				vc.append("vAttenuation").append(i).append(" = 1.0 / (uLightAttenuation").append(i).append("[1] + uLightAttenuation").append(i).append("[2] * dist + uLightAttenuation").append(i).append("[3] * dist * dist);\n");
				fc.append("L = normalize(uLightPosition").append(i).append(" - V.xyz);\n");
			} else if(light.getLightType() == ALight.SPOT_LIGHT) {
				vc.append("dist = distance(V.xyz, uLightPosition").append(i).append(");\n");
				vc.append("vAttenuation").append(i).append(" = (uLightAttenuation").append(i).append("[1] + uLightAttenuation").append(i).append("[2] * dist + uLightAttenuation").append(i).append("[3] * dist * dist);\n");
				fc.append("L = normalize(uLightPosition").append(i).append(" - V.xyz);\n");
				fc.append("vec3 spotDir").append(i).append(" = normalize(-uLightDirection").append(i).append(");\n");
				fc.append("float spot_factor").append(i).append(" = dot( L, spotDir").append(i).append(" );\n");
				fc.append("if( uSpotCutoffAngle").append(i).append(" < 180.0 ) {\n");
					fc.append("if( spot_factor").append(i).append(" >= cos( radians( uSpotCutoffAngle").append(i).append(") ) ) {\n");
						fc.append("spot_factor").append(i).append(" = (1.0 - (1.0 - spot_factor").append(i).append(") * 1.0/(1.0 - cos( radians( uSpotCutoffAngle").append(i).append("))));\n");
						fc.append("spot_factor").append(i).append(" = pow(spot_factor").append(i).append(", uSpotFalloff").append(i).append("* 1.0/spot_factor").append(i).append(");\n");
					fc.append("}\n");
					fc.append("else {\n");
						fc.append("spot_factor").append(i).append(" = 0.0;\n");
					fc.append("}\n");
					fc.append("L = vec3(L.x, L.y, L.z) * spot_factor").append(i).append(";\n");
					fc.append("}\n");
			} else if(light.getLightType() == ALight.DIRECTIONAL_LIGHT) {
				vc.append("vAttenuation").append(i).append(" = 1.0;\n");
				fc.append("L = normalize(-uLightDirection").append(i).append(");\n");
			}

			fc.append("NdotL = max(dot(N, L), 0.1);\n");
			fc.append("power = uLightPower").append(i).append(" * NdotL * vAttenuation").append(i).append(";\n");
			fc.append("intensity += power;\n"); 
			
			fc.append("Kd.rgb += uLightColor").append(i).append(" * power;\n");
		}
		
		super.setShaders(vertexShader.replace("%LIGHT_CODE%", vc.toString()), fragmentShader.replace("%LIGHT_CODE%", fc.toString()));
	}
}