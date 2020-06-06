//
//  StylizeHairMaskFilter.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class StylizeHairMaskFilter: FritzVisionImageFilter {

  // Use the original image as the predictor
  public let compositionMode = FilterCompositionMode.overlayOnOriginalImage
  
  public let hairModel: FritzVisionHairSegmentationModelFast
  public let styleModel: FritzVisionStylePredictor
  
  public let hairOptions: FritzVisionSegmentationMaskOptions
  public let styleOptions: FritzVisionStyleModelOptions
  
  public init(
    hairModel: FritzVisionHairSegmentationModelFast,
    styleModel: FritzVisionStylePredictor,
    hairOptions: FritzVisionSegmentationMaskOptions = .init(),
    styleOptions: FritzVisionStyleModelOptions = .init()
  ) {
    self.hairModel = hairModel
    self.styleModel = styleModel
    self.hairOptions = hairOptions
    self.styleOptions = styleOptions
  }
  
  public func process(_ image: FritzVisionImage) -> FritzVisionFilterResult {
    do {
      // Run predictions on the input image
      let hairResult = try hairModel.predict(image)
      let styleBuffer = try styleModel.predict(image, options: styleOptions)
      let stylizedImage = FritzVisionImage(ciImage: CIImage(cvPixelBuffer: styleBuffer))
      
      // Create mask of the hair and extract the corresponding area in the stylized image
      if let mask = hairResult.buildSingleClassMask(
        forClass: FritzVisionHairClass.hair,
        options: hairOptions,
        resize: false
        ),
        let styleMasked = stylizedImage.masked(with: mask) {
        
        return .success(FritzVisionImage(image: styleMasked))
      }
      return .failure(FritzVisionVideoError.invalidPrediction)
    }
    catch let error {
      return .failure(error)
    }
  }
}
