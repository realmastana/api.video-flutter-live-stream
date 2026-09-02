import 'dart:ui';

import 'package:flutter_video_live_stream/flutter_video_live_stream.dart';
import 'package:flutter_video_live_stream_example/utils/set.dart';

Map<Size, String> inflateResolutionsMap() {
  return PredefinedResolution.values
      .map((e) => e.resolution)
      .toSet()
      .toDisplayMap(valueTransformation: (e) => e.toPrettyString());
}

extension PredefinedResolutionPrettifier on PredefinedResolution {
  String toPrettyString() {
    return this.resolution.toPrettyString();
  }
}

extension SizePrettifier on Size {
  String toPrettyString() {
    return "${this.width.toInt()}x${this.height.toInt()}";
  }
}
