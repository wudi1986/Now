package com.clien;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


/**
 * @author travel
 * 编写编码器的注意事项：
 * 1、 mina 为 IoSession 写队列里的每个对象调用 ProtocolEncode.encode 方法。
 * 因为业务处理器里写出的都是与编码器对应高层对象，所以可以直接进行类型转换。
 * 2、从 jvm 堆分配 IoBuffer，最好避免使用直接缓存，因为堆缓存一般有更好的性能。
 * 3、开发人员不需要释放缓存， mina 会释放。
 * 4、在 dispose 方法里可以释放编码所需的资源。
 */
public class ResponseEncoder extends ProtocolEncoderAdapter {

  @Override
  public void encode(IoSession session, Object message,
      ProtocolEncoderOutput out) throws Exception {
    // TODO Auto-generated method stub
	byte[] dataBytes = message.toString().getBytes("UTF-8");
    byte[] sizeBytes = NumberUtil.int2bytes(dataBytes.length);
    
    IoBuffer buffer = IoBuffer.allocate(256);
    buffer.setAutoExpand(true);
    
    buffer.put(sizeBytes);
    buffer.put(dataBytes);
    
    buffer.flip();
    out.write(buffer);
    out.flush();
    
    buffer.free();
  }

}