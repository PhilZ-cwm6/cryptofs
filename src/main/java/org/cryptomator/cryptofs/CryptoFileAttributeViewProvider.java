package org.cryptomator.cryptofs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.HashMap;
import java.util.Map;

class CryptoFileAttributeViewProvider {

	private final Map<Class<? extends FileAttributeView>, FileAttributeViewProvider<? extends FileAttributeView>> fileAttributeViewProviders = new HashMap<>();
	private final CryptoFileAttributeProvider fileAttributeProvider;

	public CryptoFileAttributeViewProvider(CryptoFileAttributeProvider fileAttributeProvider) {
		fileAttributeViewProviders.put(BasicFileAttributeView.class, (FileAttributeViewProvider<BasicFileAttributeView>) CryptoBasicFileAttributeView::new);
		fileAttributeViewProviders.put(PosixFileAttributeView.class, (FileAttributeViewProvider<PosixFileAttributeView>) CryptoPosixFileAttributeView::new);
		fileAttributeViewProviders.put(DosFileAttributeView.class, (FileAttributeViewProvider<DosFileAttributeView>) CryptoDosFileAttributeView::new);
		this.fileAttributeProvider = fileAttributeProvider;
	}

	@SuppressWarnings("unchecked")
	public <A extends FileAttributeView> A getAttributeView(Path ciphertextPath, Class<A> type) throws IOException {
		if (fileAttributeViewProviders.containsKey(type)) {
			FileAttributeViewProvider<A> provider = (FileAttributeViewProvider<A>) fileAttributeViewProviders.get(type);
			return provider.provide(ciphertextPath, fileAttributeProvider);
		} else {
			throw new UnsupportedOperationException("Unsupported file attribute type: " + type);
		}
	}

	@FunctionalInterface
	private static interface FileAttributeViewProvider<A extends FileAttributeView> {
		A provide(Path ciphertextPath, CryptoFileAttributeProvider fileAttributeProvider);
	}

}