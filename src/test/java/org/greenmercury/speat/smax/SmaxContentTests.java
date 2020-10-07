package org.greenmercury.speat.smax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SmaxContentTests {

  @Test
  void testConstructor() {
    SmaxContent content;
    content = new SmaxContent();
    assertEquals(0, content.length());
    content = new SmaxContent("abcdefghi".subSequence(3, 6));
    assertEquals("def", content.toString());
    content = new SmaxContent("abcdefghi");
    assertEquals("abcdefghi", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("defghi", content.toString());
    content = new SmaxContent(content, 0, 3);
    assertEquals("def", content.toString());
  }
  
  @Test
  void testCompareTo() {
    String[][] comPairs = {
        {"a", "a"},
        {"a", "b"},
        {"b", "a"},
        {"abcd", "abcde"},
        {"b", "abcd"},
        {"abcdef", "abcdef"},
        {"", ""}
    };
    for (String[] comPair : comPairs) {
      assertEquals(comPair[0].compareTo(comPair[1]),
          new SmaxContent(comPair[0]).compareTo(new SmaxContent(comPair[1])));
      if (comPair[0].length() > 3) {
        StringBuffer comPairBuf0 = new StringBuffer(comPair[0]);
        StringBuffer comPairBuf1 = new StringBuffer(comPair[1]);
        assertEquals(comPair[0].substring(1, 3).compareTo(comPair[1].substring(1, 3)),
            new SmaxContent(comPairBuf0, 1, 3).compareTo(new SmaxContent(comPairBuf1, 1, 3)));
      }
    }
  }
  
  @Test
  void testLenght() {
    SmaxContent content;
    content = new SmaxContent();
    assertEquals(0, content.length());
    content = new SmaxContent("abcdefghi".subSequence(3, 6));
    assertEquals(3, content.length());
    content = new SmaxContent("abcdefghi");
    assertEquals(9, content.length());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals(6, content.length());
    content = new SmaxContent(content, 0, 3);
    assertEquals(3, content.length());
  }
  
  @Test
  void testCharAt() {
    SmaxContent content;
    content = new SmaxContent("abcdefghi");
    assertEquals('a', content.charAt(0));
    assertEquals('d', content.charAt(3));
    assertEquals('i', content.charAt(8));
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals('d', content.charAt(0));
    assertEquals('g', content.charAt(3));
    assertEquals('i', content.charAt(5));
  }

  @Test
  void testSubSequence() {
    SmaxContent content;
    content = new SmaxContent("abcdefghi");
    assertEquals("", content.subSequence(0, 0).toString());
    assertEquals("abc", content.subSequence(0, 3).toString());
    assertEquals("def", content.subSequence(3, 6).toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("def", content.subSequence(0, 3).toString());
    assertEquals("ghi", content.subSequence(3, 6).toString());
    assertEquals("fgh", content.subSequence(2, 5).toString());
  }
  
  @Test
  void testAppend() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("defghijkl", content.append("jkl").toString());
    assertEquals("defghijkl", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 2, 8);
    assertEquals("cdefghlm", content.append("jklmnn", 2, 4).toString());
    assertEquals("cdefghlm", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("defghix", content.append('x').toString());
    assertEquals("defghix", content.toString());
  }
  
  @Test
  void testGetChars() {
    char[] dst = new char[10];
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    content.getChars(2, 5, dst, 1);
    assertEquals("fgh", String.copyValueOf(dst, 1, 3));
  }
  
  @Test
  void testSetCharAt() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    content.setCharAt(1, 'x');
    content.setCharAt(3, 'y');
    assertEquals("dxfyhi", content.toString());
  }
  
  @Test
  void testDelete() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dhi", content.delete(1, 4).toString());
    assertEquals("dhi", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("defghi", content.delete(5, 5).toString());
    assertEquals("defghi", content.toString());
  }
  
  @Test
  void testDeleteCharAt() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dfghi", content.deleteCharAt(1).toString());
    assertEquals("dghi", content.deleteCharAt(1).toString());
    assertEquals("dghi", content.toString());
  }
  
  @Test
  void testReplace() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dxyzzyxghi", content.replace(1, 3, "xyzzyx").toString());
    assertEquals("dxyzzyxghi", content.toString());
  }
  
  @Test
  void testSubstring() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("hi", content.substring(4).toString());
    assertEquals("defghi", content.toString());
    assertEquals("efg", content.substring(1, 4).toString());
    assertEquals("defghi", content.toString());
  }
  
  @Test
  void testInsert() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dexyzfghi", content.insert(2, "xyz").toString());
    assertEquals("dexyzfghi", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dexyzfghi", content.insert(2, new SmaxContent("xyz")).toString());
    assertEquals("dexyzfghi", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dexyzfghi", content.insert(2, new SmaxContent(new SmaxContent("ppxyzqq"), 2, 5)).toString());
    assertEquals("dexyzfghi", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dexyzfghi", content.insert(2, new SmaxContent(new SmaxContent("ppxyzqq"), 2, 5)).toString());
    assertEquals("dexyzfghi", content.toString());
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals("dexyzfghi", content.insert(2, new SmaxContent("ppxyzqq"), 2, 5).toString());
    assertEquals("dexyzfghi", content.toString());
  }
  
  @Test
  void testIndexOf() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghi"), 3, 9);
    assertEquals(-1, content.indexOf("b"));
    assertEquals(0, content.indexOf("d"));
    assertEquals(1, content.indexOf("ef"));
    content = new SmaxContent(new StringBuffer("abcdefghiabcdefghi"), 3, 15);
    assertEquals(7, content.indexOf("b", 6));
    assertEquals(9, content.indexOf("d", 6));
    assertEquals(1, content.indexOf("ef"));
    assertEquals(10, content.indexOf("ef", 6));
  }
  
  @Test
  void testLastIndexOf() {
    SmaxContent content;
    content = new SmaxContent(new StringBuffer("abcdefghiabcdefghi"), 3, 15);
    assertEquals(7, content.lastIndexOf("b"));
    assertEquals(-1, content.lastIndexOf("b", 6));
    assertEquals(9, content.lastIndexOf("d"));
    assertEquals(0, content.lastIndexOf("d", 6));
    assertEquals(10, content.lastIndexOf("ef"));
    assertEquals(1, content.lastIndexOf("ef", 6));
  }
}
