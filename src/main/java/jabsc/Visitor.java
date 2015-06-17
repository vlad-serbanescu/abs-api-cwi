package jabsc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import abs.api.Actor;
import bnfc.abs.AbstractVisitor;
import bnfc.abs.Absyn.AnyImport;
import bnfc.abs.Absyn.ClassBody;
import bnfc.abs.Absyn.ClassDecl;
import bnfc.abs.Absyn.ClassImplements;
import bnfc.abs.Absyn.ClassParamDecl;
import bnfc.abs.Absyn.ClassParamImplements;
import bnfc.abs.Absyn.Decl;
import bnfc.abs.Absyn.EAnd;
import bnfc.abs.Absyn.EEq;
import bnfc.abs.Absyn.ExtendsDecl;
import bnfc.abs.Absyn.FieldAssignClassBody;
import bnfc.abs.Absyn.FieldClassBody;
import bnfc.abs.Absyn.Import;
import bnfc.abs.Absyn.InterfDecl;
import bnfc.abs.Absyn.ListQType;
import bnfc.abs.Absyn.MethClassBody;
import bnfc.abs.Absyn.MethSig;
import bnfc.abs.Absyn.Modul;
import bnfc.abs.Absyn.Module;
import bnfc.abs.Absyn.Par;
import bnfc.abs.Absyn.Param;
import bnfc.abs.Absyn.Prog;
import bnfc.abs.Absyn.QTyp;
import bnfc.abs.Absyn.QType;
import bnfc.abs.Absyn.QTypeSegmen;
import bnfc.abs.Absyn.QTypeSegment;
import bnfc.abs.Absyn.TSimple;
import bnfc.abs.Absyn.Type;

/**
 * The visitor for all possible nodes in the AST of an ABS
 * program.
 */
class Visitor extends AbstractVisitor<Prog, JavaWriter> {

  private static final String ABS_API_ACTOR_CLASS = Actor.class.getName();
  private static final Set<Modifier> DEFAULT_MODIFIERS = Collections.singleton(Modifier.PUBLIC);
  private static final Set<Modifier> DEFAULT_FIELD_MODIFIERS =
      new HashSet<>(Arrays.asList(Modifier.PRIVATE, Modifier.FINAL));

  private final Set<String> moduleNames;
  private final Prog prog;
  private final JavaWriterSupplier javaWriterSupplier;
  private final String packageName;

  /**
   * Ctor.
   * 
   * @param packageName the package spec of the program
   * @param prog the parsed {@link Prog} AST node
   * @param javaWriterSupplier the {@link JavaWriterSupplier}
   *        for each top-level element
   */
  public Visitor(String packageName, Prog prog, JavaWriterSupplier javaWriterSupplier) {
    this.packageName = packageName;
    this.prog = prog;
    this.javaWriterSupplier = javaWriterSupplier;
    this.moduleNames = new HashSet<>();
  }

  @Override
  public Prog visit(Prog p, JavaWriter w) {
    try {
      for (Module module : ((Prog) p).listmodule_) {
        moduleNames.add(getQTypeName(((Modul) module).qtype_));
        module.accept(this, w);
        w.emitEmptyLine();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return prog;
  }

  @Override
  public Prog visit(Modul m, JavaWriter w) {
    try {
      w.emitPackage(packageName);
      for (Import imprt : m.listimport_) {
        imprt.accept(this, w);
      }
      for (Decl decl : m.listdecl_) {
        JavaWriter jw = createJavaWriter(decl, w);
        decl.accept(this, jw);
        jw.emitEmptyLine();
        close(jw, w);
      }
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(AnyImport p, JavaWriter w) {
    return prog;
  }

  @Override
  public Prog visit(InterfDecl id, JavaWriter w) {
    try {
      final String identifier = id.uident_;
      beginElementKind(w, ElementKind.INTERFACE, identifier, DEFAULT_MODIFIERS, null, null);
      w.emitEmptyLine();
      id.listmethsignat_.forEach(sig -> visit((MethSig) sig, w));
      w.endType();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(ExtendsDecl ed, JavaWriter w) {
    try {
      final String identifier = ed.uident_;
      beginElementKind(w, ElementKind.INTERFACE, identifier, DEFAULT_MODIFIERS, null,
          toList(ed.listqtype_));
      w.emitEmptyLine();
      ed.listmethsignat_.forEach(sig -> sig.accept(this, w));
      w.endType();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(ClassDecl p, JavaWriter w) {
    try {
      final String identifier = p.uident_;
      beginElementKind(w, ElementKind.CLASS, identifier, DEFAULT_MODIFIERS, null, null);
      w.emitEmptyLine();
      for (ClassBody cb : p.listclassbody_1) {
        cb.accept(this, w);
      }
      for (ClassBody cb : p.listclassbody_2) {
        cb.accept(this, w);
      }
      w.endType();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(ClassImplements p, JavaWriter w) {
    try {
      final String identifier = p.uident_;
      beginElementKind(w, ElementKind.CLASS, identifier, DEFAULT_MODIFIERS, null,
          toList(p.listqtype_));
      w.emitEmptyLine();
      for (ClassBody cb : p.listclassbody_1) {
        cb.accept(this, w);
      }
      for (ClassBody cb : p.listclassbody_2) {
        cb.accept(this, w);
      }
      w.endType();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(ClassParamDecl cpd, JavaWriter w) {
    try {
      final String identifier = cpd.uident_;
      beginElementKind(w, ElementKind.CLASS, identifier, DEFAULT_MODIFIERS, null, null);
      w.emitEmptyLine();
      List<String> parameters = new ArrayList<>();
      for (Param param : cpd.listparam_) {
        Par p = (Par) param;
        parameters.add(getTypeName(p.type_));
        parameters.add(p.lident_);
        w.emitField(getTypeName(p.type_), p.lident_, DEFAULT_FIELD_MODIFIERS);
      }
      w.emitEmptyLine();
      w.beginConstructor(DEFAULT_MODIFIERS, parameters, null);
      for (Param param : cpd.listparam_) {
        Par p = (Par) param;
        w.emitStatement("this." + p.lident_ + " = " + p.lident_);
      }
      w.endConstructor();
      for (ClassBody cb : cpd.listclassbody_1) {
        cb.accept(this, w);
      }
      for (ClassBody cb : cpd.listclassbody_2) {
        cb.accept(this, w);
      }
      w.endType();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(ClassParamImplements cpi, JavaWriter w) {
    try {
      final String identifier = cpi.uident_;
      beginElementKind(w, ElementKind.CLASS, identifier, DEFAULT_MODIFIERS, null,
          toList(cpi.listqtype_));
      w.emitEmptyLine();
      List<String> parameters = new ArrayList<>();
      for (Param param : cpi.listparam_) {
        Par p = (Par) param;
        parameters.add(getTypeName(p.type_));
        parameters.add(p.lident_);
        w.emitField(getTypeName(p.type_), p.lident_, DEFAULT_FIELD_MODIFIERS);
      }
      w.emitEmptyLine();
      w.beginConstructor(DEFAULT_MODIFIERS, parameters, null);
      for (Param param : cpi.listparam_) {
        Par p = (Par) param;
        w.emitStatement("this." + p.lident_ + " = " + p.lident_);
      }
      w.endConstructor();
      for (ClassBody cb : cpi.listclassbody_1) {
        cb.accept(this, w);
      }
      for (ClassBody cb : cpi.listclassbody_2) {
        cb.accept(this, w);
      }
      w.endType();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(MethSig ms, JavaWriter w) {
    try {
      String returnType = getTypeName(ms.type_);
      String name = ms.lident_;
      List<String> parameters = new ArrayList<>();
      for (Param param : ms.listparam_) {
        Par p = (Par) param;
        parameters.add(getTypeName(p.type_));
        parameters.add(p.lident_);
      }
      w.beginMethod(returnType, name, DEFAULT_MODIFIERS, parameters, Collections.emptyList());
      w.endMethod();
      w.emitEmptyLine();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public Prog visit(FieldClassBody p, JavaWriter w) {
    try {
      String fType = getTypeName(p.type_);
      w.emitField(fType, p.lident_);
      w.emitEmptyLine();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Prog visit(FieldAssignClassBody p, JavaWriter w) {
    try {
      String fType = getTypeName(p.type_);
      p.pureexp_.accept(this, w);
      w.emitField(fType, p.lident_, DEFAULT_MODIFIERS, "TODO:visit PureExpresion");
      w.emitEmptyLine();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(MethClassBody p, JavaWriter w) {
    try {
      String returnType = getTypeName(p.type_);
      String name = p.lident_;
      List<String> parameters = new ArrayList<>();
      for (Param param : p.listparam_) {
        Par p1 = (Par) param;
        parameters.add(getTypeName(p1.type_));
        parameters.add(p1.lident_);
      }
      w.beginMethod(returnType, name, DEFAULT_MODIFIERS, parameters, Collections.emptyList());
      p.block_.accept(this, w);
      w.endMethod();
      w.emitEmptyLine();
      return prog;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Prog visit(EAnd e, JavaWriter w) {
    try {
      e.pureexp_1.accept(this, w);
      w.emit(" && ");
      e.pureexp_2.accept(this, w);
      return prog;
    } catch (IOException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public Prog visit(EEq e, JavaWriter w) {
    try {
      e.pureexp_1.accept(this, w);
      w.emit(" == ");
      e.pureexp_2.accept(this, w);
      return prog;
    } catch (IOException x) {
      throw new RuntimeException(x);
    }
  }

  /**
   * @param qtype
   * @return
   */
  protected String getQTypeName(QType qtype) {
    if (qtype instanceof QTyp) {
      QTyp qtyp = (QTyp) qtype;
      QTypeSegment qtypesegment_ = qtyp.listqtypesegment_.iterator().next();
      if (qtypesegment_ instanceof QTypeSegmen) {
        QTypeSegmen qTypeSegmen = (QTypeSegmen) qtypesegment_;
        return qTypeSegmen.uident_;
      }
    }
    return null;
  }

  /**
   * @param qtypes
   * @return
   */
  protected List<String> toList(ListQType qtypes) {
    return qtypes.stream().map(qtype -> getQTypeName(qtype)).collect(Collectors.toList());
  }

  /**
   * Begin a Java type.
   * 
   * @param w the Java writer
   * @param kind See {@link ElementKind}
   * @param identifier the Java identifier of the type
   * @param modifiers the set of {@link Modifier}s
   * @param classParentType the extending type that can be
   *        <code>null</code>
   * @param implementingInterfaces the implementing interface
   *        that can be <code>null</code>
   * @throws IOException Exception from {@link JavaWriter}
   * @throws IllegalArgumentException if kind other than "class"
   *         or "interface" is requested
   */
  protected void beginElementKind(JavaWriter w, ElementKind kind, String identifier,
      Set<Modifier> modifiers, String classParentType, Collection<String> implementingInterfaces)
          throws IOException {
    switch (kind) {
      case CLASS:
        Set<String> implementsTypes = new HashSet<>();
        implementsTypes.add(ABS_API_ACTOR_CLASS);
        if (implementingInterfaces != null && !implementingInterfaces.isEmpty()) {
          implementsTypes.addAll(implementingInterfaces);
        }
        w.beginType(identifier, kind.name().toLowerCase(), modifiers, classParentType,
            implementsTypes.toArray(new String[0]));
        return;
      case INTERFACE:
        w.beginType(identifier, kind.name().toLowerCase(), modifiers, ABS_API_ACTOR_CLASS,
            new String[0]);
        return;
      default:
        throw new IllegalArgumentException("Unsupported Java element kind: " + kind);
    }
  }

  /**
   * @param decl
   * @param w
   * @return
   * @throws IOException
   */
  protected JavaWriter createJavaWriter(Decl decl, JavaWriter w) throws IOException {
    if (isTopLevel(decl)) {
      String identifier = getTopLevelDeclIdentifier(decl);
      if (packageName.equalsIgnoreCase(identifier) || moduleNames.contains(identifier)) {
        return w;
      }
      JavaWriter jw = javaWriterSupplier.apply(identifier);
      jw.emitPackage(packageName);
      return jw;
    } else {
      return w;
    }
  }

  /**
   * @param childWriter
   * @param parentWriter
   * @throws IOException
   */
  protected void close(JavaWriter childWriter, JavaWriter parentWriter) throws IOException {
    if (childWriter != parentWriter) {
      childWriter.close();
    }
  }

  /**
   * @param decl
   * @return
   */
  protected boolean isTopLevel(Decl decl) {
    return decl instanceof ClassDecl || decl instanceof ClassImplements
        || decl instanceof ClassParamDecl || decl instanceof ClassParamImplements
        || decl instanceof ExtendsDecl || decl instanceof InterfDecl;
  }

  /**
   * @param decl
   * @return
   */
  protected <C extends Decl> String getTopLevelDeclIdentifier(Decl decl) {
    if (decl instanceof ClassDecl) {
      return ((ClassDecl) decl).uident_;
    }
    if (decl instanceof ClassImplements) {
      return ((ClassImplements) decl).uident_;
    }
    if (decl instanceof ClassParamDecl) {
      return ((ClassParamDecl) decl).uident_;
    }
    if (decl instanceof ClassParamImplements) {
      return ((ClassParamImplements) decl).uident_;
    }
    if (decl instanceof ExtendsDecl) {
      return ((ExtendsDecl) decl).uident_;
    }
    if (decl instanceof InterfDecl) {
      return ((InterfDecl) decl).uident_;
    }
    throw new IllegalArgumentException("Unknown top level type: " + decl);
  }

  private String getTypeName(Type type) {
    if (type instanceof TSimple) {
      TSimple ts = (TSimple) type;
      QType qtype_ = ts.qtype_;
      return getQTypeName(qtype_);
    }
    return null;
  }

}
