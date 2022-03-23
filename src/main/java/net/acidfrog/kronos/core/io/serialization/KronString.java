package net.acidfrog.kronos.core.io.serialization;

import static net.acidfrog.kronos.core.io.serialization.KronParser.*;

public class KronString extends KronContainer {

    public int count;
	private char[] characters;
	
	private KronString() {
		super(KronContainer.Type.STRING);
		size += KronField.Type.sizeOf(KronField.Type.BYTE) + KronField.Type.sizeOf(KronField.Type.INT);
	}
	
	public static KronString create(String name, String data) {
		KronString string = new KronString();
		string.setName(name);
		string.count = data.length();
		string.characters = data.toCharArray();
		string.updateSize();
		return string;
	}
	
	public int getBytes(byte[] dest, int pointer) {
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, count);
		pointer = writeBytes(dest, pointer, characters);
		return pointer;
	}
	
	public int getDataSize() {
		return characters.length * KronField.Type.sizeOf(KronField.Type.CHAR);
	}

	private void updateSize() {
		size += getDataSize();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return new String(characters);
	}
    
}
