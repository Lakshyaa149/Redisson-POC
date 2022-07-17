
package com.laksh.poc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.codec.MarshallingCodec;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Codec class for compression data using gzip.
 */


public class GZipCodec extends BaseCodec {
    private final Codec innerCodec;
    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try (GZIPInputStream inputStream = new GZIPInputStream(new ByteBufInputStream(buf));
                 ByteBufOutputStream bos = new ByteBufOutputStream(out)) {
                byte[] buffer = new byte[buf.readableBytes()];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                return innerCodec.getValueDecoder().decode(out, state);
            } finally {
                out.release();
            }
        }
    };


    private final Encoder encoder = new Encoder() {
        @Override
        public ByteBuf encode(Object in) throws IOException {
            ByteBuf buf = innerCodec.getValueEncoder().encode(in);
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try (GZIPOutputStream outputStream = new GZIPOutputStream(new ByteBufOutputStream(out))) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                outputStream.write(bytes);
                return out;
            } finally {
                buf.release();
            }
        }
    };

/**
     * Constructor with default redission codec.
     */

    public GZipCodec() {
        this(new MarshallingCodec());
    }


/**
     * Constructor with inner codec param.
     *
     * @param innerCodec
     */

    public GZipCodec(Codec innerCodec) {
        this.innerCodec = innerCodec;
    }


    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

}

