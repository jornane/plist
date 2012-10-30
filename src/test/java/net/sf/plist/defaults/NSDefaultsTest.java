package net.sf.plist.defaults;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sf.plist.NSObject;
import net.sf.plist.NSString;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import org.junit.After;
import org.junit.Test;

public final class NSDefaultsTest {

	private static final String DOMAIN = NSDefaultsTest.class.getPackage().getName();
	private NSDefaults defaults;
	private Scope scope;

	@After
	public void tearDown() {
		try {
			defaults.clear();
			defaults.commit();
		} catch (Exception e) {
			// Do nothing, if this is a problem we will figure it out later
		}
	}
	
	@Test
	public void userTest() throws PropertyListException, IOException {
		defaults = NSDefaults.getInstance(DOMAIN, scope = Scope.getUserScope());
		test();
	}
	
	@Test
	public void userByHostTest() throws PropertyListException, IOException {
		defaults = NSDefaults.getInstance(DOMAIN, scope = Scope.getUserByHostScope());
		test();
	}
	
	private void test() throws PropertyListException, IOException {
		// Call tearDown to make sure the file is clean
		tearDown();
		
		defaults.put("foo", new NSString("bar"));
		defaults.commit();
		defaults.put("bar", new NSString("foo"));
		OperatingSystemPath osp = OperatingSystemPath.getInstance();
		NSObject obj = PropertyListParser.parse(
				new File(osp.getPListPath(Scope.USER).toString()+
				File.separator +
				(scope.isByHost() ? (osp.isLowerCasePreferred() ? "byhost" : "ByHost") + File.separator : "") +
				DOMAIN +
				(scope.isByHost() ? "." + osp.getMachineUUID() : "") +
				".plist"));
		// Check if writing succeeded
		assertEquals(new NSString("bar"), obj.toMap().get("foo"));
		// Check if a non-existing key indeed doesn't exist (checks #clear() )
		assertEquals(1, obj.toMap().size());
		// Check if a key written after #commit() indeed doesn't exist
		assertNull(obj.toMap().get("bar"));
	}
	
	@Test
	public void getSystemTest() {
		defaults = NSDefaults.getInstance(DOMAIN, scope = Scope.getSystemScope());
		NSDefaults.getDomains(); // Check that it doesn't crash.
	}
	
	@Test
	public void testScopes() {
		// Should at least have the three big ones;
		// User, User by Host or System
		assertTrue(Scope.instances().length >= 3);
	}
	
	@Test
	public void testCustomUUID() {
		defaults = NSDefaults.getInstance(DOMAIN, scope = Scope.getUserByHostScope(DOMAIN));
		NSDefaults.getDomains(); // Check that it doesn't crash.
	}
	
	@Test
	public void domainTest() {
		NSDefaults.addDomain(DOMAIN);
		assertEquals(DOMAIN, NSDefaults.getDomainForName(DOMAIN+".sub"));
	}

}
