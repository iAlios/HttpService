package com.alios.httpservices.service.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.alios.httpservices.service.AssetFile;
import com.alios.httpservices.utils.MLog;

public class HttpServer extends NanoHTTPD {
	/**
	 * Common mime type for dynamic content: binary
	 */
	public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
	/**
	 * Default Index file names.
	 */
	@SuppressWarnings("serial")
	public static final List<String> INDEX_FILE_NAMES = new ArrayList<String>() {
		{
			add("index.html");
			add("index.htm");
		}
	};
	/**
	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
	 */
	@SuppressWarnings("serial")
	private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
		{
			put("css", "text/css");
			put("htm", "text/html");
			put("html", "text/html");
			put("xml", "text/xml");
			put("java", "text/x-java-source, text/java");
			put("md", "text/plain");
			put("txt", "text/plain");
			put("asc", "text/plain");
			put("gif", "image/gif");
			put("jpg", "image/jpeg");
			put("jpeg", "image/jpeg");
			put("png", "image/png");
			put("mp3", "audio/mpeg");
			put("m3u", "audio/mpeg-url");
			put("mp4", "video/mp4");
			put("ogv", "video/ogg");
			put("flv", "video/x-flv");
			put("mov", "video/quicktime");
			put("swf", "application/x-shockwave-flash");
			put("js", "application/javascript");
			put("pdf", "application/pdf");
			put("doc", "application/msword");
			put("ogg", "application/x-ogg");
			put("zip", "application/octet-stream");
			put("exe", "application/octet-stream");
			put("class", "application/octet-stream");
		}
	};

	private static Map<String, IHttpPlugin> mimeTypeHandlers = new HashMap<String, IHttpPlugin>();
	private final List<File> mFileRootDirs;
	private final List<AssetFile> mAssetRootDirs;
	private final boolean quiet;

	private AssetManager mAssetManager = null;
	private String mAssetRootFile;

	public HttpServer(AssetManager cAssetManager, int port,
			String assetWwwroot, File fileDirRoot) {
		this(cAssetManager, null, port, assetWwwroot, fileDirRoot, true);
	}

	public HttpServer(AssetManager cAssetManager, int port, File fileDirRoot) {
		this(cAssetManager, null, port, null, fileDirRoot, true);
	}

	public HttpServer(AssetManager cAssetManager, int port, String wwwroot,
			File fileDirRoot, boolean quiet) {
		this(cAssetManager, null, port, wwwroot, fileDirRoot, quiet);
	}

	public HttpServer(AssetManager cAssetManager, String host, int port,
			String wwwroot, File fileDirRoot, boolean quiet) {
		super(host, port);
		this.mAssetManager = cAssetManager;
		this.quiet = quiet;
		this.mFileRootDirs = new ArrayList<File>();
		if (fileDirRoot != null) {
			this.mFileRootDirs.add(fileDirRoot);
		}
		this.mAssetRootFile = wwwroot;
		this.mAssetRootDirs = new ArrayList<AssetFile>();
		addAssetRoot(wwwroot);
	}

	private void ergodicAssetDir(AssetManager cAssetManager, String dir) {
		try {
			String paths[] = cAssetManager.list(dir);
			String subDirPath = dir + File.separator;
			String subDir = null;
			for (String path : paths) {
				subDir = subDirPath + path;
				if (isAssetDir(cAssetManager, subDir)) {
					ergodicAssetDir(cAssetManager, subDir);
				} else {
					this.mAssetRootDirs.add(new AssetFile(false, subDir));
				}
			}
		} catch (IOException e) {
			MLog.d("!!! Can not ergodic the asset dir: "
					+ e.getLocalizedMessage(), e);
		}
	}

	private boolean isAssetDir(AssetManager cAssetManager, String path) {
		try {
			String[] list = cAssetManager.list(path);
			if (list.length > 0) {
				return true;
			}
		} catch (IOException e) {
			MLog.d("!!! can not list the file: " + e.getLocalizedMessage(), e);
		}
		return false;
	}

	public void addAssetRoot(String wwwroot) {
		if (!TextUtils.isEmpty(wwwroot)) {
			ergodicAssetDir(mAssetManager, wwwroot);
		}
	}

	/**
	 * 导入插件
	 * 
	 * @param indexFiles
	 * @param mimeType
	 * @param plugin
	 * @param commandLineOptions
	 */
	public static void registerPluginForMimeType(String[] indexFiles,
			String mimeType, IHttpPlugin plugin,
			Map<String, String> commandLineOptions) {
		if (mimeType == null || plugin == null) {
			return;
		}

		if (indexFiles != null) {
			for (String filename : indexFiles) {
				int dot = filename.lastIndexOf('.');
				if (dot >= 0) {
					String extension = filename.substring(dot + 1)
							.toLowerCase();
					MIME_TYPES.put(extension, mimeType);
				}
			}
			INDEX_FILE_NAMES.addAll(Arrays.asList(indexFiles));
		}
		mimeTypeHandlers.put(mimeType, plugin);
		plugin.initialize(commandLineOptions);
	}

	public File getFileRootDir() {
		return mFileRootDirs.get(0);
	}

	private List<File> getFileRootDirs() {
		return mFileRootDirs;
	}

	public void addWwwRootDir(File wwwroot) {
		mFileRootDirs.add(wwwroot);
	}

	/**
	 * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
	 * instead of '+'.
	 */
	private String encodeUri(String uri) {
		String newUri = "";
		StringTokenizer st = new StringTokenizer(uri, "/ ", true);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equals("/"))
				newUri += "/";
			else if (tok.equals(" "))
				newUri += "%20";
			else {
				try {
					newUri += URLEncoder.encode(tok, "UTF-8");
				} catch (UnsupportedEncodingException ignored) {
				}
			}
		}
		return newUri;
	}

	public Response serve(IHTTPSession session) {
		Map<String, String> header = session.getHeaders();
		Map<String, String> parms = session.getParms();
		String uri = session.getUri();

		if (!quiet) {
			MLog.d(session.getMethod() + " '" + uri + "' ");

			Iterator<String> e = header.keySet().iterator();
			while (e.hasNext()) {
				String value = e.next();
				MLog.d("  HDR: '" + value + "' = '" + header.get(value) + "'");
			}
			e = parms.keySet().iterator();
			while (e.hasNext()) {
				String value = e.next();
				MLog.d("  PRM: '" + value + "' = '" + parms.get(value) + "'");
			}
		}

		for (File homeDir : getFileRootDirs()) {
			// Make sure we won't die of an exception later
			if (!homeDir.isDirectory()) {
				return createResponse(Response.Status.INTERNAL_ERROR,
						NanoHTTPD.MIME_PLAINTEXT,
						"INTERNAL ERRROR: given path is not a directory ("
								+ homeDir + ").");
			}
		}
		return respond(Collections.unmodifiableMap(header), uri);
	}

	private Response respond(Map<String, String> headers, String uri) {
		// Remove URL arguments
		uri = uri.trim().replace(File.separatorChar, '/');
		if (uri.indexOf('?') >= 0) {
			uri = uri.substring(0, uri.indexOf('?'));
		}

		// Prohibit getting out of current directory
		if (uri.startsWith("src/main") || uri.endsWith("src/main")
				|| uri.contains("../")) {
			return createResponse(Response.Status.FORBIDDEN,
					NanoHTTPD.MIME_PLAINTEXT,
					"FORBIDDEN: Won't serve ../ for security reasons.");
		}

		AssetFile cAssetfile = findIndexFileInDirectory(mAssetManager, uri);
		if (cAssetfile == null) {
			boolean canServeUri = false;
			File homeDir = null;
			List<File> roots = getFileRootDirs();
			for (int i = 0; !canServeUri && i < roots.size(); i++) {
				homeDir = roots.get(i);
				canServeUri = canServeUri(uri, homeDir);
			}
			if (!canServeUri) {
				return createResponse(Response.Status.NOT_FOUND,
						NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
			}

			// Browsers get confused without '/' after the directory, send a
			// redirect.
			File f = new File(homeDir, uri);
			if (f.isDirectory() && !uri.endsWith("/")) {
				uri += "/";
				Response res = createResponse(Response.Status.REDIRECT,
						NanoHTTPD.MIME_HTML,
						"<html><body>Redirected: <a href=\"" + uri + "\">"
								+ uri + "</a></body></html>");
				res.addHeader("Location", uri);
				return res;
			}

			if (f.isDirectory()) {
				// First look for index files (index.html, index.htm, etc) and
				// if
				// none found, list the directory if readable.
				String indexFile = findIndexFileInDirectory(f);
				if (indexFile == null) {
					if (f.canRead()) {
						// No index file, list the directory if it is readable
						return createResponse(Response.Status.OK,
								NanoHTTPD.MIME_HTML, listDirectory(uri, f));
					} else {
						return createResponse(Response.Status.FORBIDDEN,
								NanoHTTPD.MIME_PLAINTEXT,
								"FORBIDDEN: No directory listing.");
					}
				} else {
					return respond(headers, uri + indexFile);
				}
			}
			String mimeTypeForFile = getMimeTypeForFile(uri);
			IHttpPlugin plugin = mimeTypeHandlers.get(mimeTypeForFile);
			Response response = null;
			if (plugin != null) {
				response = plugin.serveFile(uri, headers, f, mimeTypeForFile);
				if (response != null && response instanceof UploadFileResponse) {
					UploadFileResponse rewrite = (UploadFileResponse) response;
					return respond(rewrite.getHeaders(), rewrite.getUri());
				}
			} else {
				response = serveFile(uri, headers, f, mimeTypeForFile);
			}
			return response != null ? response : createResponse(
					Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
					"Error 404, file not found.");
		} else {
			String mimeTypeForFile = getMimeTypeForFile(cAssetfile.mPath);
			Response response = serveFile(mAssetManager, uri, headers,
					cAssetfile.mPath, mimeTypeForFile);
			return response != null ? response : createResponse(
					Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
					"Error 404, file not found.");
		}
	}

	private Response serveFile(AssetManager cAssetManager, String uri,
			Map<String, String> header, String file, String mime) {
		Response res;
		try {
			InputStream cInputStream = cAssetManager.open(file);
			long fileLen = cInputStream.available();
			// Calculate etag
			String etag = Integer.toHexString((file + "" + fileLen).hashCode());

			// Support (simple) skipping:
			long startFrom = 0;
			long endAt = -1;
			String range = header.get("range");
			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try {
						if (minus > 0) {
							startFrom = Long.parseLong(range
									.substring(0, minus));
							endAt = Long.parseLong(range.substring(minus + 1));
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}

			// Change return code and add Content-Range header when skipping is
			// requested
			if (range != null && startFrom >= 0) {
				if (startFrom >= fileLen) {
					res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE,
							NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
					res.addHeader("ETag", etag);
				} else {
					if (endAt < 0) {
						endAt = fileLen - 1;
					}
					long newLen = endAt - startFrom + 1;
					if (newLen < 0) {
						newLen = 0;
					}

					final long dataLen = newLen;
					cInputStream.skip(startFrom);

					res = createResponse(Response.Status.PARTIAL_CONTENT, mime,
							cInputStream);
					res.addHeader("Content-Length", "" + dataLen);
					res.addHeader("Content-Range", "bytes " + startFrom + "-"
							+ endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match")))
					res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
				else {
					res = createResponse(Response.Status.OK, mime, cInputStream);
					res.addHeader("Content-Length", "" + fileLen);
					res.addHeader("ETag", etag);
				}
			}
		} catch (IOException ioe) {
			res = createResponse(Response.Status.FORBIDDEN,
					NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
		}

		return res;
	}

	private boolean canServeUri(String uri, File homeDir) {
		boolean canServeUri;
		File f = new File(homeDir, uri);
		canServeUri = f.exists();
		if (!canServeUri) {
			String mimeTypeForFile = getMimeTypeForFile(uri);
			IHttpPlugin plugin = mimeTypeHandlers.get(mimeTypeForFile);
			if (plugin != null) {
				canServeUri = plugin.canServeUri(uri, homeDir);
			}
		}
		return canServeUri;
	}

	/**
	 * Serves file from homeDir and its' subdirectories (only). Uses only URI,
	 * ignores all headers and HTTP parameters.
	 */
	Response serveFile(String uri, Map<String, String> header, File file,
			String mime) {
		Response res;
		try {
			// Calculate etag
			String etag = Integer.toHexString((file.getAbsolutePath()
					+ file.lastModified() + "" + file.length()).hashCode());

			// Support (simple) skipping:
			long startFrom = 0;
			long endAt = -1;
			String range = header.get("range");
			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try {
						if (minus > 0) {
							startFrom = Long.parseLong(range
									.substring(0, minus));
							endAt = Long.parseLong(range.substring(minus + 1));
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}

			// Change return code and add Content-Range header when skipping is
			// requested
			long fileLen = file.length();
			if (range != null && startFrom >= 0) {
				if (startFrom >= fileLen) {
					res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE,
							NanoHTTPD.MIME_PLAINTEXT, "");
					res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
					res.addHeader("ETag", etag);
				} else {
					if (endAt < 0) {
						endAt = fileLen - 1;
					}
					long newLen = endAt - startFrom + 1;
					if (newLen < 0) {
						newLen = 0;
					}

					final long dataLen = newLen;
					FileInputStream fis = new FileInputStream(file) {
						@Override
						public int available() throws IOException {
							return (int) dataLen;
						}
					};
					fis.skip(startFrom);

					res = createResponse(Response.Status.PARTIAL_CONTENT, mime,
							fis);
					res.addHeader("Content-Length", "" + dataLen);
					res.addHeader("Content-Range", "bytes " + startFrom + "-"
							+ endAt + "/" + fileLen);
					res.addHeader("ETag", etag);
				}
			} else {
				if (etag.equals(header.get("if-none-match")))
					res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
				else {
					res = createResponse(Response.Status.OK, mime,
							new FileInputStream(file));
					res.addHeader("Content-Length", "" + fileLen);
					res.addHeader("ETag", etag);
				}
			}
		} catch (IOException ioe) {
			res = createResponse(Response.Status.FORBIDDEN,
					NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
		}

		return res;
	}

	// Get MIME type from file name extension, if possible
	private String getMimeTypeForFile(String uri) {
		int dot = uri.lastIndexOf('.');
		String mime = null;
		if (dot >= 0) {
			mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
		}
		return mime == null ? MIME_DEFAULT_BINARY : mime;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType,
			InputStream message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	// Announce that the file server accepts partial content requests
	private Response createResponse(Response.Status status, String mimeType,
			String message) {
		Response res = new Response(status, mimeType, message);
		res.addHeader("Accept-Ranges", "bytes");
		return res;
	}

	private AssetFile findIndexFileInDirectory(AssetManager cAssetManager,
			String url) {
		String dir = mAssetRootFile + url;
		if (dir.endsWith("/")) {
			dir = dir.substring(0, dir.length() - 1);
		}
		if (dir.matches(".*[.].*$")) { // 目录名称中不能存在"."
			for (AssetFile file : mAssetRootDirs) {
				if (file.mPath.equals(dir)) {
					return file;
				}
			}
		} else {
			for (AssetFile file : mAssetRootDirs) {
				for (String fileName : INDEX_FILE_NAMES) {
					if (file.mPath.equals(dir + File.separator + fileName)) {
						return file;
					}
				}
			}
		}
		return null;
	}

	private String findIndexFileInDirectory(File directory) {
		for (String fileName : INDEX_FILE_NAMES) {
			File indexFile = new File(directory, fileName);
			if (indexFile.exists()) {
				return fileName;
			}
		}
		return null;
	}

	private String listDirectory(String uri, File f) {
		String heading = "Directory " + uri;
		StringBuilder msg = new StringBuilder("<html><head><title>" + heading
				+ "</title><style><!--\n"
				+ "span.dirname { font-weight: bold; }\n"
				+ "span.filesize { font-size: 75%; }\n" + "// -->\n"
				+ "</style>" + "</head><body><h1>" + heading + "</h1>");

		String up = null;
		if (uri.length() > 1) {
			String u = uri.substring(0, uri.length() - 1);
			int slash = u.lastIndexOf('/');
			if (slash >= 0 && slash < u.length()) {
				up = uri.substring(0, slash + 1);
			}
		}

		List<String> files = Arrays.asList(f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile();
			}
		}));
		Collections.sort(files);
		List<String> directories = Arrays.asList(f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		}));
		Collections.sort(directories);
		if (up != null || directories.size() + files.size() > 0) {
			msg.append("<ul>");
			if (up != null || directories.size() > 0) {
				msg.append("<section class=\"directories\">");
				if (up != null) {
					msg.append("<li><a rel=\"directory\" href=\"")
							.append(up)
							.append("\"><span class=\"dirname\">..</span></a></b></li>");
				}
				for (String directory : directories) {
					String dir = directory + "/";
					msg.append("<li><a rel=\"directory\" href=\"")
							.append(encodeUri(uri + dir))
							.append("\"><span class=\"dirname\">").append(dir)
							.append("</span></a></b></li>");
				}
				msg.append("</section>");
			}
			if (files.size() > 0) {
				msg.append("<section class=\"files\">");
				for (String file : files) {
					msg.append("<li><a href=\"").append(encodeUri(uri + file))
							.append("\"><span class=\"filename\">")
							.append(file).append("</span></a>");
					File curFile = new File(f, file);
					long len = curFile.length();
					msg.append("&nbsp;<span class=\"filesize\">(");
					if (len < 1024) {
						msg.append(len).append(" bytes");
					} else if (len < 1024 * 1024) {
						msg.append(len / 1024).append(".")
								.append(len % 1024 / 10 % 100).append(" KB");
					} else {
						msg.append(len / (1024 * 1024)).append(".")
								.append(len % (1024 * 1024) / 10 % 100)
								.append(" MB");
					}
					msg.append(")</span></li>");
				}
				msg.append("</section>");
			}
			msg.append("</ul>");
		}
		msg.append("</body></html>");
		return msg.toString();
	}
}
