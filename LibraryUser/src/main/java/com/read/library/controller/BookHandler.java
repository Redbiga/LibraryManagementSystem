package com.read.library.controller;

import com.alibaba.fastjson.JSONObject;
import com.read.library.entitys.Book;
import com.reda.library.entitys.Borrow;
import com.reda.library.entitys.Type;
import com.reda.library.repository.BookRepository;
import com.reda.library.repository.BorrowRepository;
import com.reda.library.repository.TypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * 图书控制器
 * @author redA
 */
@RestController
@RequestMapping("/user/book")
public class BookHandler {

    /**
     * 图书数据库操作接口
     */
    @Resource
    private   BookRepository bookRepository;

    /**
     * 借阅图书数据库操作接口
     */
    @Resource
    private BorrowRepository borrowRepository;

    /**
     * 图书种类数据库接口
     */
    @Resource
    private TypeRepository typeRepository;
    /**
     * 分页查询所有书籍
     * @return 书籍列表
     */
    @GetMapping("/all")
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * 仪表盘信息
     * @return 仪表盘信息
     */
    @GetMapping("/dashboard")
    public JSONObject getDashboard() {
        JSONObject dashboardInfo = new JSONObject();

        // 图书数量
        List<Book> bookList = bookRepository.findAll();
        Integer bookCount = 0;
        for (Book book : bookList) {
            bookCount += book.getQuantity();
        }

        // 借阅图书数量
        List<Borrow> borrowList = borrowRepository.findAll();
        Integer borrowCount = borrowList.size();

        // 超时未归还图书数量
        Integer overtimeCount = 0;
        Calendar calendar = Calendar.getInstance();
        for (Borrow borrow : borrowList) {
            if (calendar.getTime().after(borrow.getReturnTime())) {
                overtimeCount++;
            }
        }

        // 存入信息
        dashboardInfo.put("bookCount", bookCount);
        dashboardInfo.put("borrowCount", borrowCount);
        dashboardInfo.put("overtimeCount", overtimeCount);

        return dashboardInfo;
    }

    /**
     * 获取图书数据
     * @param page 页码
     * @param size 大小
     * @return 图书数据
     */
    @GetMapping("/{page}/{size}")
    public Page<Book> getPageBook(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        return bookRepository.findAll(pageable);
    }

    /**
     * 图书信息搜索
     * @param text 搜索信息
     * @return 图书信息
     */
    @GetMapping("/search/{type}/{text}/{page}/{size}")
    public Page<Book> findBook(@PathVariable("type") String type,
                                 @PathVariable("text") String text,
                                 @PathVariable("page") Integer page,
                                 @PathVariable("size") Integer size) {
        Page<Book> bookPage;
        PageRequest pageable = PageRequest.of(page - 1, size);
        switch (type) {
            case "name":
                bookPage = bookRepository.findByNameLike(text, pageable);
                break;
            case "author":
                bookPage = bookRepository.findByAuthorLike(text, pageable);
                break;
            case "press":
                bookPage = bookRepository.findByPressLike(text, pageable);
                break;
            case "isbn":
                bookPage = bookRepository.findByIsbnLike(text, pageable);
                break;
            default:
                bookPage = null;
                break;
        }

        return bookPage;
    }

    /**
     * 图书种类获取
     * @return 图书种类列表
     */
    @GetMapping("/type")
    public List<Type> getType() {
        return typeRepository.findAll();
    }
    /**
     * ISBN号码搜索图书
     * @param isbn ISBN号码
     * @return 图书信息
     */
    @GetMapping("/find/isbn/{isbn}")
    public Book findByIsbn(@PathVariable("isbn") String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
}
