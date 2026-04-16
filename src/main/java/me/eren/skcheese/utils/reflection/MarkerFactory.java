package me.eren.skcheese.utils.reflection;

import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessFlag;

public class MarkerFactory {

	public static Marker generate(String markerName) throws Throwable {
		ClassFile cf = ClassFile.of();

		String packageName = MarkerFactory.class.getPackageName();
		ClassDesc classDesc = ClassDesc.of(packageName + ".Marker_" + markerName);

		ClassDesc interfaceDesc = ClassDesc.of(Marker.class.getName());

		byte[] bytes = cf.build(classDesc, classBuilder -> {
			classBuilder.withInterfaceSymbols(interfaceDesc);
			classBuilder.withFlags(AccessFlag.PUBLIC);

			// constructor
			classBuilder.withMethod(
					"<init>",
					MethodTypeDesc.of(ClassDesc.ofDescriptor("V")),
					0,
					mb -> mb.withCode(codeBuilder -> {
						codeBuilder.aload(0);
						codeBuilder.invokespecial(
								ClassDesc.ofDescriptor("Ljava/lang/Object;"),
								"<init>",
								MethodTypeDesc.of(ClassDesc.ofDescriptor("V"))
						);
						codeBuilder.return_();
					})
			);

			// void run(Runnable) {
			//   runnable.run()
			// }
			classBuilder.withMethod(
					"run",
					MethodTypeDesc.of(ClassDesc.ofDescriptor("V"), ClassDesc.ofDescriptor("Ljava/lang/Runnable;")),
					AccessFlag.PUBLIC.mask(),
					mb -> mb.withCode(codeBuilder -> {
						codeBuilder.aload(1);
						codeBuilder.invokeinterface(
								ClassDesc.ofDescriptor("Ljava/lang/Runnable;"),
								"run",
								MethodTypeDesc.of(ClassDesc.ofDescriptor("V"))
						);
						codeBuilder.return_();
					})
			);
		});

		// load the class
		Class<?> clazz = MethodHandles.lookup()
				.defineHiddenClass(bytes, true)
				.lookupClass();

		return (Marker) MethodHandles.lookup()
				.findConstructor(clazz, MethodType.methodType(void.class))
				.invoke();
	}

}
