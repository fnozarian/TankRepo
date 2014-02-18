package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import java.util.ArrayList;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Matrix3f;
import com.jme3.scene.Spatial;

public class Tank {

    private PhysicsHoverControl vehicleControl;

    public void setVehicleControl(PhysicsHoverControl vehicleControl) {
        this.vehicleControl = vehicleControl;
    }
    private final float accelerationForce = 1000.0f;
    private final float brakeForce = 100.0f;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private float compValue = 0.2f; //(lower than damp!)
    private float dampValue = 0.3f;
    private final float mass = 400;
    private float stiffness = 120.0f;//200=f1 car
    private ArrayList<Weapon> weapons;
    private Weapon activeWeapon;
    private int health;
    private CollisionShape colShape;
    private AudioNode tankIdleSound;
    private Node tankNode;
    private Spatial tankBody;
    private CameraNode camNode;
    private SimpleApplication app;

    public Tank(Application app) {

        this.app = (SimpleApplication)app;
        tankNode = new Node();
        
        //Configuring Model
        tankBody = this.app.getAssetManager().loadModel("Models/HoverTank/Tank2.mesh.xml");
        colShape = CollisionShapeFactory.createDynamicMeshShape(tankBody);
        tankBody.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        tankBody.setLocalTranslation(new Vector3f(0, 0, 0));///-60, 14, -23
        tankBody.setLocalRotation(new Quaternion(new float[]{0, 0.01f, 0}));
        tankNode.attachChild(tankBody);


        //Configuring camera
        camNode = new CameraNode("camNode", this.app.getCamera());
        //Setting the direction to Spatial to camera, this means the camera will copy the movements of the Node
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(0f, 4f, -12f);
        tankNode.attachChild(camNode);

        //Configuring vehicle control
        vehicleControl = new PhysicsHoverControl(colShape, 500);
        vehicleControl.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        tankNode.addControl(vehicleControl);

        //Configuring tank sound
        tankIdleSound = new AudioNode(this.app.getAssetManager(), "Sounds/propeller-plane-idle.wav", false);
        tankIdleSound.setLooping(true);
        tankNode.attachChild(tankIdleSound);
        tankIdleSound.play();
        
        //Adding to screen
        this.app.getRootNode().attachChild(tankNode);
        getPhysicsSpace().add(tankNode);
    }

    void accelerate(float force) {
        vehicleControl.accelerate(force);
    }

    void brake(float force) {
        
    }

    void steer(float value) {
        vehicleControl.steer(value);
    }

    public void fire() {
        Vector3f fireDirection = tankNode.getWorldRotation().getRotationColumn(2);
        activeWeapon.fire(fireDirection);
    }

    public void switchWeapon(Weapon weapon) {
        activeWeapon = weapons.get(weapons.indexOf(weapon));
    }
    
    
    void decreaseHealth(int point) {
    }

    void increaseHealth(int point) {
    }
    //must be destroied ?
    public Node getTankNode() {
        return tankNode;
    }

    /**
     * Reset the place of tank to it's initial state
     */
    public void resetTank() {
        vehicleControl.setPhysicsLocation(new Vector3f(-140, 14, -23));
        vehicleControl.setPhysicsRotation(new Matrix3f());
        vehicleControl.clearForces();
    }
    public void addWeapon(Weapon weapon){
        weapons.add(weapon);
        //do something in game e.g show a gun in w
    }
    public void attachToWorld(Node rooNode){
        
    }
    private PhysicsSpace getPhysicsSpace() {
        return this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
    }
}
