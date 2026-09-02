import 'package:flutter_video_live_stream/flutter_video_live_stream.dart';
import 'package:flutter_video_live_stream_example/utils/set.dart';

Map<Channel, String> inflateChannelsMap() {
  return Channel.values
      .toSet()
      .toDisplayMap(valueTransformation: (e) => e.toPrettyString());
}

extension ChannelPrettifier on Channel {
  String toPrettyString() {
    return switch (this) {
      Channel.mono => "mono",
      Channel.stereo => "stereo",
    };
  }
}
