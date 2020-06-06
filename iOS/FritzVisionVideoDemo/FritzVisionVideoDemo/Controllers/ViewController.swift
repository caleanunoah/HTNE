//
//  ViewController.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import UIKit
import Fritz

class ViewController: UITableViewController {

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .lightContent
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    self.view.backgroundColor = .black
    title = "Video Processing".uppercased()
    tableView.register(DemoTableViewCell.self, forCellReuseIdentifier: "DemoTableViewCell")
    tableView.register(LinkTableViewCell.self, forCellReuseIdentifier: "LinkTableViewCell")
    clearsSelectionOnViewWillAppear = true
  }

  override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    if let _ = tableView.cellForRow(at: indexPath) as? LinkTableViewCell,
      let url = URL(string: "https://app.fritz.ai/register") {
      UIApplication.shared.open(url)
      tableView.deselectRow(at: indexPath, animated: true)
    }

    if let cell = tableView.cellForRow(at: indexPath) as? DemoTableViewCell {
      guard let identifier = cell.reuseIdentifier else { return }

      var firstStrategy: VideoFilterStrategy?
      var secondStrategy: VideoFilterStrategy?
      switch identifier {
      case "StarryNightHair":
        firstStrategy = StylizeHairStrategy()
      case "StylizeBackground":
        firstStrategy = StylizeBackgroundStrategy()
      case "ObjectPose":
        firstStrategy = ObjectPoseStrategy()
      case "DoubleStyle":
        firstStrategy = DoubleStyleStrategy()
      case "ObjectDoubleMask":
        firstStrategy = PoseDoubleMaskStrategy()
      case "SplitStyle":
        firstStrategy = FemmesStrategy()
        secondStrategy = ScreamStrategy()
      default:
        return
      }

      // Start view controller and set strategies depending on the selected cell
      if let firstStrategy = firstStrategy, let secondStrategy = secondStrategy {
        let storyboard = UIStoryboard(name: "SplitScreenStoryboard", bundle: Bundle.main)
        let viewController = storyboard.instantiateViewController(withIdentifier: "SplitScreen")
          as! SplitScreenViewController
        viewController.topStrategy = firstStrategy
        viewController.bottomStrategy = secondStrategy
        self.navigationController?.pushViewController(viewController, animated: true)
      }
      else if let firstStrategy = firstStrategy {
        let storyboard = UIStoryboard(name: "SingleScreenStoryboard", bundle: Bundle.main)
        let viewController = storyboard.instantiateViewController(withIdentifier: "SingleScreen")
            as! SingleScreenViewController
        viewController.videoStrategy = firstStrategy
        self.navigationController?.pushViewController(viewController, animated: true)
      }
    }
  }
}
