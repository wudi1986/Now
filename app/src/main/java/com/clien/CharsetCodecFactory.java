package com.clien;



import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CharsetCodecFactory implements ProtocolCodecFactory {

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {

		return new RequestDecoder();

	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {

		return new ResponseEncoder();

	}

}
