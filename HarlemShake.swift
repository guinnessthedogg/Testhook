import UIKit
import CoreMotion

class InteractiveViewController: UIViewController {
    var animator: UIDynamicAnimator!
    var gravity: UIGravityBehavior!
    var collision: UICollisionBehavior!
    var itemBehavior: UIDynamicItemBehavior!
    var motionManager: CMMotionManager!

    private var attachmentBehavior: UIAttachmentBehavior?
    private var previousTouchPoint: CGPoint?
    private var touchedView: UIView?

    override func viewDidLoad() {
        super.viewDidLoad()
        setupAnimatorAndBehaviors()
        setupMotionManager()
        startShakingAllInteractiveViews()
    }

    func setupAnimatorAndBehaviors() {
        animator = UIDynamicAnimator(referenceView: view)
        gravity = UIGravityBehavior()
        collision = UICollisionBehavior()
        collision.translatesReferenceBoundsIntoBoundary = true
        itemBehavior = UIDynamicItemBehavior()
        itemBehavior.elasticity = 0.75
        itemBehavior.friction = 0.5
        itemBehavior.resistance = 0.5

        animator.addBehavior(gravity)
        animator.addBehavior(collision)
        animator.addBehavior(itemBehavior)
    }

    func setupMotionManager() {
        motionManager = CMMotionManager()
        if motionManager.isDeviceMotionAvailable {
            motionManager.startDeviceMotionUpdates(to: .main) { [weak self] (motion, error) in
                if let motion = motion, error == nil {
                    let gravityVector = CGVector(dx: motion.gravity.x * 3, dy: -motion.gravity.y * 3)
                    self?.gravity.gravityDirection = gravityVector
                }
            }
        }
    }

    func startShakingAllInteractiveViews() {
        for view in self.view.subviews where view.isUserInteractionEnabled {
            startWobbleAnimation(on: view)
            DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
                self.stopWobbleAnimation(on: view)
                self.add(itemToPhysics: view)
                self.addRandomSpin(to: view)
            }
        }
    }

    func startWobbleAnimation(on view: UIView) {
        let animation = CAKeyframeAnimation(keyPath: "transform")
        let wobbleAngle: CGFloat = 0.3

        let valLeft = CATransform3DMakeRotation(-wobbleAngle, 0, 0, 1)
        let valRight = CATransform3DMakeRotation(wobbleAngle, 0, 0, 1)

        animation.values = [NSValue(caTransform3D: CATransform3DIdentity),
                            NSValue(caTransform3D: valLeft),
                            NSValue(caTransform3D: CATransform3DIdentity),
                            NSValue(caTransform3D: valRight),
                            NSValue(caTransform3D: CATransform3DIdentity)]

        animation.autoreverses = true
        animation.duration = 0.15
        animation.repeatCount = Float.infinity

        view.layer.add(animation, forKey: "wobbleAnimation")
    }

    func stopWobbleAnimation(on view: UIView) {
        view.layer.removeAnimation(forKey: "wobbleAnimation")
    }

    func add(itemToPhysics item: UIView) {
        gravity.addItem(item)
        collision.addItem(item)
        itemBehavior.addItem(item)
    }

    func addRandomSpin(to view: UIView) {
        let angularVelocity = CGFloat.random(in: -3...3)
        itemBehavior.addAngularVelocity(angularVelocity, for: view)
    }

    // Override touch methods
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first, let viewTouched = touch.view, viewTouched.isUserInteractionEnabled {
            touchedView = viewTouched
            previousTouchPoint = touch.location(in: view)

            if let previousTouchPoint = previousTouchPoint {
                let centerOffset = UIOffset(horizontal: (previousTouchPoint.x - viewTouched.bounds.midX),
                                            vertical: (previousTouchPoint.y - viewTouched.bounds.midY))
                attachmentBehavior = UIAttachmentBehavior(item: viewTouched, offsetFromCenter: centerOffset, attachedToAnchor: previousTouchPoint)
                if let attachmentBehavior = attachmentBehavior {
                    animator.addBehavior(attachmentBehavior)
                }
            }
        }
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touch = touches.first, let touchedView = touchedView, let previousTouchPoint = previousTouchPoint {
            let newTouchPoint = touch.location(in: view)
            let offsetFromPrevious = CGPoint(x: newTouchPoint.x - previousTouchPoint.x, y: newTouchPoint.y - previousTouchPoint.y)

            touchedView.center = CGPoint(x: touchedView.center.x + offsetFromPrevious.x, y: touchedView.center.y + offsetFromPrevious.y)
            attachmentBehavior?.anchorPoint = newTouchPoint
            self.previousTouchPoint = newTouchPoint
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let touchedView = touchedView, let attachmentBehavior = attachmentBehavior {
            animator.removeBehavior(attachmentBehavior)

            if let touch = touches.first, let previousTouchPoint = previousTouchPoint {
                let newTouchPoint = touch.location(in: view)
                let velocity = CGPoint(x: newTouchPoint.x - previousTouchPoint.x, y: newTouchPoint.y - previousTouchPoint.y)
                flingView(touchedView, withVelocity: velocity)
            }

            self.touchedView = nil
            self.previousTouchPoint = nil
        }
    }

    private func flingView(_ view: UIView, withVelocity velocity: CGPoint) {
        let magnitude = sqrt(velocity.x * velocity.x + velocity.y * velocity.y)
        if magnitude > 20 {
            let push = UIPushBehavior(items: [view], mode: .instantaneous)
            push.pushDirection = CGVector(dx: velocity.x / 10, dy: velocity.y / 10)
            push.magnitude = magnitude / 200
            animator.addBehavior(push)
        }

        add(itemToPhysics: view)
    }
}
