//
//  DraggableKeypoint.swift
//  FritzHandPoseEstimationDemo
//
//  Created by Steven Yeung on 11/20/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import UIKit

protocol DragDelegate: class {

  /// Triggers an action after dragging a keypoint.
  ///
  /// - Parameters:
  ///   - posePart: The part of the keypoint dragged.
  ///   - position: Where the keypoint was dragged to.
  func didDrag(_ posePart: HandSkeleton, to position: CGPoint)
}

class DraggableKeypoint: UIView {
  let pointRadius: CGFloat = 40
  let posePart: HandSkeleton!
  weak var delegate: DragDelegate?

  public init(
    position: CGPoint,
    posePart: HandSkeleton
  ) {
    self.posePart = posePart
    super.init(frame: .zero)

    self.frame = CGRect(
      origin: CGPoint(x: position.x - (pointRadius / 2), y: position.y - (pointRadius / 2)),
      size: CGSize(width: pointRadius, height: pointRadius)
    )

    // Make the view draggable
    let dragRecognizer = UIPanGestureRecognizer(target:self, action:#selector(dragRecognized))
    self.isUserInteractionEnabled = true
    self.addGestureRecognizer(dragRecognizer)
    self.layer.cornerRadius = CGFloat(pointRadius / 2)
    self.backgroundColor = .red
  }

  required init?(coder aDecoder: NSCoder) {
    self.posePart = nil
    super.init(coder: aDecoder)
  }

  @objc func dragRecognized(_ recognizer: UIPanGestureRecognizer) {
    let translation = recognizer.translation(in: superview)

    // Move the keypoint to the new position
    let newPositionX = center.x + translation.x
    let newPositionY = center.y + translation.y
    self.center = CGPoint(x: newPositionX, y: newPositionY)
    recognizer.setTranslation(.zero, in: superview)
    delegate?.didDrag(posePart, to: center)
  }
}
