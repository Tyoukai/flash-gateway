package com.fast.gateway.utils;

import com.google.protobuf.*;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc;
import io.grpc.reflection.v1alpha.ServerReflectionRequest;
import io.grpc.reflection.v1alpha.ServerReflectionResponse;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.SPILT_SLASH;
import static com.fast.gateway.utils.Constants.SPLIT_POINT;


/**
 * grpc相关的反射util
 */
public class GrpcUtils {

    /**
     * 缓存文件描述的方法，
     * 全限定服务名称 -> 对应的文件描述
     */
    private static Map<String, Descriptors.FileDescriptor> CACHED_FILE_DESCRIPTOR = new ConcurrentHashMap<>();

    /**
     * 同步阻塞调用
     *
     *  缓存方法描述，有新服务产生时，只有第一次调用耗时会高一点
     *
     * @param ip
     * @param port
     * @param request 原始请求报文 json格式
     * @param method 全限定方法名称
     * @return
     */
    public static String blockingGrpcGeneralizedCall(String ip, String port, String request, String method) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, Integer.valueOf(port))
                .usePlaintext()
                .build();

        String fullServiceName = extraPrefix(method);
        String methodName = extraSuffix(method);
        String packageName = extraPackagePrefix(fullServiceName);
        String serviceName = extraServiceName(fullServiceName);

        // 根据响应解析 FileDescriptor
        Descriptors.FileDescriptor fileDescriptor = getFileDescriptor(method, fullServiceName, packageName, serviceName, channel);

        // 查找服务描述
        Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.getFile().findServiceByName(serviceName);
        // 查找方法描述
        Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName(methodName);

        // 发起请求
        try {
            return executeCall(channel, fileDescriptor, methodDescriptor, request);
        } catch (Exception e) {
            // 出现异常后，将缓存的文件描述删除，异常可以是因为rpc文件描述发生了变化，
            // 具体文件描述发生变化后会抛出什么异常之后再优化
            CACHED_FILE_DESCRIPTOR.remove(fullServiceName);
            e.printStackTrace();
        }
        return null;
    }

    private static Descriptors.FileDescriptor getFileDescriptor(String method, String fullServiceName, String packageName, String serviceName, ManagedChannel channel) {
        Descriptors.FileDescriptor fileDescriptor = CACHED_FILE_DESCRIPTOR.computeIfAbsent(fullServiceName, (name) -> {
            List<Descriptors.FileDescriptor> fileDescriptors = new ArrayList<>();
            StreamObserver<ServerReflectionResponse> requestStreamObserver = new StreamObserver<ServerReflectionResponse>() {
                @Override
                public void onNext(ServerReflectionResponse response) {
                    // 处理响应
                    try {
                        if (response.getMessageResponseCase() == ServerReflectionResponse.MessageResponseCase.FILE_DESCRIPTOR_RESPONSE) {
                            List<ByteString> fileDescriptorProtoList = response.getFileDescriptorResponse().getFileDescriptorProtoList();
                            // 根据响应解析 FileDescriptor
                            fileDescriptors.add(0, getFileDescriptor(fileDescriptorProtoList, packageName, serviceName));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }
                @Override
                public void onCompleted() {
                    System.out.println("onCompleted");
                }
            };

            ServerReflectionGrpc.ServerReflectionStub stub = ServerReflectionGrpc.newStub(channel);
            StreamObserver<ServerReflectionRequest> client= stub.serverReflectionInfo(requestStreamObserver);

            ServerReflectionRequest serverReflectionRequest = ServerReflectionRequest.newBuilder()
                    .setFileContainingSymbol(method.replace('/', '.'))
                    .build();

            client.onNext(serverReflectionRequest);
            try {
                channel.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return fileDescriptors.get(0);
        });
        return fileDescriptor;
    }

    /**
     * grpc的泛化调用
     *
     * @param ip 具体服务的ip
     * @param port 具体服务的端口
     * @param request 原始的请求报文
     * @param method 具体服务的方法名称
     * @return grpc的请求响应
     */
    public static String grpcGeneralizedCall(String ip, String port, String request, String method) {

        String newMethod = method.replace("/", ".");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ip, Integer.valueOf(port))
                .usePlaintext()
                .build();

        ServerReflectionGrpc.ServerReflectionStub stub = ServerReflectionGrpc.newStub(channel);

        List<String> messages = new ArrayList<>();

        StreamObserver<ServerReflectionResponse> requestStreamObserver = new StreamObserver<ServerReflectionResponse>() {
            @Override
            public void onNext(ServerReflectionResponse response) {
                // 处理响应
                try {
                    if (response.getMessageResponseCase() == ServerReflectionResponse.MessageResponseCase.FILE_DESCRIPTOR_RESPONSE) {
                        List<ByteString> fileDescriptorProtoList = response.getFileDescriptorResponse().getFileDescriptorProtoList();
                        messages.add(0, handleResponse(fileDescriptorProtoList, channel, method, request));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        };

        StreamObserver<ServerReflectionRequest> client= stub.serverReflectionInfo(requestStreamObserver);

        ServerReflectionRequest serverReflectionRequest = ServerReflectionRequest.newBuilder()
                .setFileContainingSymbol(newMethod)
                .build();

        client.onNext(serverReflectionRequest);
        try {
            channel.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (messages.size() != 0) {
            return messages.get(0);
        }
        return "{\"code\": 0,\"msg\": \"success\"}";

    }


    private static String handleResponse(List<ByteString> fileDescriptorProtoList,
                                         ManagedChannel channel,
                                         String methodFullName,
                                         String requestContent) {
        try {
            // 解析方法和服务名称
            String fullServiceName = extraPrefix(methodFullName);
            String methodName = extraSuffix(methodFullName);
            String packageName = extraPackagePrefix(fullServiceName);
            String serviceName = extraServiceName(fullServiceName);

            // 根据响应解析 FileDescriptor
            Descriptors.FileDescriptor fileDescriptor = getFileDescriptor(fileDescriptorProtoList, packageName, serviceName);

            // 查找服务描述
            Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.getFile().findServiceByName(serviceName);
            // 查找方法描述
            Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName(methodName);

            // 发起请求
            return executeCall(channel, fileDescriptor, methodDescriptor, requestContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取前缀
     */
    private static String extraPrefix(String content) {
        int index = content.lastIndexOf(SPILT_SLASH);
        return content.substring(0, index);
    }

    private static String extraPackagePrefix(String content) {
        int index = content.lastIndexOf(SPLIT_POINT);
        return content.substring(0, index);
    }

    /**
     * 获取后缀
     */
    private static String extraSuffix(String content) {
        int index = content.lastIndexOf(SPILT_SLASH);
        return content.substring(index + 1);
    }

    private static String extraServiceName(String content) {
        int index = content.lastIndexOf(SPLIT_POINT);
        return content.substring(index + 1);
    }

    private static Descriptors.FileDescriptor getFileDescriptor(List<ByteString> fileDescriptorProtoList,
                                                                String packageName,
                                                                String serviceName) throws Exception {

        Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap =
                fileDescriptorProtoList.stream()
                        .map(bs -> {
                            try {
                                return DescriptorProtos.FileDescriptorProto.parseFrom(bs);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(DescriptorProtos.FileDescriptorProto::getName, f -> f));


        if (fileDescriptorProtoMap.isEmpty()) {
            throw new IllegalArgumentException("方法的文件描述不存在");
        }

        // 查找服务对应的 Proto 描述
        DescriptorProtos.FileDescriptorProto fileDescriptorProto = findServiceFileDescriptorProto(packageName, serviceName, fileDescriptorProtoMap);

        // 获取这个 Proto 的依赖
        Descriptors.FileDescriptor[] dependencies = getDependencies(fileDescriptorProto, fileDescriptorProtoMap);

        // 生成 Proto 的 FileDescriptor
        return Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, dependencies);
    }

    /**
     * 根据包名和服务名查找相应的文件描述
     */
    private static DescriptorProtos.FileDescriptorProto findServiceFileDescriptorProto(String packageName,
                                                                                       String serviceName,
                                                                                       Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap) {
        for (DescriptorProtos.FileDescriptorProto proto : fileDescriptorProtoMap.values()) {
            if (proto.getPackage().equals(packageName)) {
                boolean exist = proto.getServiceList()
                        .stream()
                        .anyMatch(s -> serviceName.equals(s.getName()));
                if (exist) {
                    return proto;
                }
            }
        }

        throw new IllegalArgumentException("服务不存在");
    }

    /**
     * 获取依赖类型
     */
    private static Descriptors.FileDescriptor[] getDependencies(DescriptorProtos.FileDescriptorProto proto,
                                                                Map<String, DescriptorProtos.FileDescriptorProto> finalDescriptorProtoMap) {
        return proto.getDependencyList()
                .stream()
                .map(finalDescriptorProtoMap::get)
                .map(f -> toFileDescriptor(f, getDependencies(f, finalDescriptorProtoMap)))
                .toArray(Descriptors.FileDescriptor[]::new);
    }

    /**
     * 将 FileDescriptorProto 转为 FileDescriptor
     */
    private static Descriptors.FileDescriptor toFileDescriptor(DescriptorProtos.FileDescriptorProto fileDescriptorProto,
                                                               Descriptors.FileDescriptor[] dependencies) {
        try {
            return Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, dependencies);
        } catch (Descriptors.DescriptorValidationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行方法调用
     */
    private static String executeCall(ManagedChannel channel,
                                    Descriptors.FileDescriptor fileDescriptor,
                                    Descriptors.MethodDescriptor originMethodDescriptor,
                                    String requestContent) throws Exception {

        // 重新生成 MethodDescriptor
        MethodDescriptor<DynamicMessage, DynamicMessage> methodDescriptor = generateMethodDescriptor(originMethodDescriptor);

        CallOptions callOptions = CallOptions.DEFAULT;

        JsonFormat.TypeRegistry registry = JsonFormat.TypeRegistry.newBuilder()
                .add(fileDescriptor.getMessageTypes())
                .build();

        // 将请求内容由 JSON 字符串转为相应的类型
        JsonFormat.Parser parser = JsonFormat.parser().usingTypeRegistry(registry);
        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder(originMethodDescriptor.getInputType());
        parser.merge(requestContent, messageBuilder);
        DynamicMessage requestMessage = messageBuilder.build();

        // 调用，调用方式可以通过 originMethodDescriptor.isClientStreaming() 和 originMethodDescriptor.isServerStreaming() 推断
        DynamicMessage response = ClientCalls.blockingUnaryCall(channel, methodDescriptor, callOptions, requestMessage);

        // 将响应解析为 JSON 字符串
        JsonFormat.Printer printer = JsonFormat.printer()
                .usingTypeRegistry(registry)
                .includingDefaultValueFields();
        String responseContent = printer.print(response);
        System.out.println(responseContent);

        return responseContent;
    }

    /**
     * 重新生成方法描述
     */
    private static MethodDescriptor<DynamicMessage, DynamicMessage> generateMethodDescriptor(Descriptors.MethodDescriptor originMethodDescriptor) {
        // 生成方法全名
        String fullMethodName = MethodDescriptor.generateFullMethodName(originMethodDescriptor.getService().getFullName(), originMethodDescriptor.getName());
        // 请求和响应类型
        MethodDescriptor.Marshaller<DynamicMessage> inputTypeMarshaller = ProtoUtils.marshaller(DynamicMessage.newBuilder(originMethodDescriptor.getInputType())
                .buildPartial());
        MethodDescriptor.Marshaller<DynamicMessage> outputTypeMarshaller = ProtoUtils.marshaller(DynamicMessage.newBuilder(originMethodDescriptor.getOutputType())
                .buildPartial());

        // 生成方法描述, originMethodDescriptor 的 fullMethodName 不正确
        return MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setFullMethodName(fullMethodName)
                .setRequestMarshaller(inputTypeMarshaller)
                .setResponseMarshaller(outputTypeMarshaller)
                // 使用 UNKNOWN，自动修改
                .setType(MethodDescriptor.MethodType.UNKNOWN)
                .build();
    }
}
