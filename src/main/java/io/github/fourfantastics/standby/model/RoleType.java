package io.github.fourfantastics.standby.model;

public enum RoleType {
	ACTOR("Actor"),
	SCREENWRITER("Screenwriter"),
	DIRECTOR("Director"),
	ANIMATOR("Animator"),
	SOUNTRACKCOMPOSER("Soundtrack composer"),
	PRODUCER("Producer"),
	MAKEUPARTIST("Makeup artist"),
	CAMERA("Camera"),
	COSTUMEDESIGNER("Costume designer"),
	SOUNDTECHNICIAN("Sound technician"),
	EDITOR("Editor"),
	CINEMATOGRAPHER("Cinematographer"),
	GAFFER("Gaffer"),
	OTHER("Other");
   
   private String name;
   
   RoleType(String name) {
	   this.name = name;
   }
   
   public String getName() {
	   return name;
   }
   
   public String toString() {
	   return getName();
   }
}
