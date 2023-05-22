// @ts-check
import { test, expect, chromium } from '@playwright/test';
import * as fs from 'fs';

test('Login Created Teacher', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    const page1Promise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Teacher' }).click();
    const page1 = await page1Promise;
    await page1.getByLabel('Username or email address').fill('i-on-classcode-teacher@outlook.pt');
    await page1.getByLabel('Password').fill('ClassCodeTeacher');
    await page1.getByRole('button', { name: 'Sign in' }).click();
    // Detects if the page is asking for authorization by identifying the authorize button
    try{
        if (await page1.getByRole('button', {name: 'Authorize Henriquess19'}).isVisible()){
            await page1.getByRole('button', {name: 'Authorize Henriquess19'}).click();
            await page1.waitForNavigation();
        }
    }catch (e) {
        console.log('No authorization needed');
    }
    await page.waitForLoadState('load');
    while (page.url() == 'http://localhost:3000/auth/teacher') {
        await page.waitForTimeout(500);
    }
    await expect(page.url()).toBe('http://localhost:3000/menu');
    const cookieJson = JSON.stringify(await page.context().cookies())
    fs.writeFileSync('./tests/teacher_cookies.json', cookieJson)

});

test('Login Created Student', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    const page1Promise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Student' }).click();
    const page1 = await page1Promise;
    await page1.getByLabel('Username or email address').fill('i-on-classcode-student@outlook.pt');
    await page1.getByLabel('Password').fill('ClassCodeStudent');
    await page1.getByRole('button', { name: 'Sign in' }).click();
    // Detects if the page is asking for authorization by identifying the authorize button
    try{
        if (await page1.getByRole('button', {name: 'Authorize Henriquess19'}).isVisible()){
            await page1.getByRole('button', {name: 'Authorize Henriquess19'}).click();
            await page1.waitForNavigation();
        }
    }catch (e) {
        console.log('No authorization needed');
    }
    await page.waitForLoadState('load');
    while (page.url() == 'http://localhost:3000/auth/student') {
        await page.waitForTimeout(500);
    }
    await expect(page.url()).toBe('http://localhost:3000/menu');
    const cookieJson = JSON.stringify(await page.context().cookies())
    fs.writeFileSync('./tests/student_cookies.json', cookieJson)
});

test('Login Pending Teacher', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    const page1Promise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Teacher' }).click();
    const page1 = await page1Promise;
    await page1.getByLabel('Username or email address').fill('i-on-classcode-pending-teacher@outlook.pt');
    await page1.getByLabel('Password').fill('ClassCodePendingTeacher');
    await page1.getByRole('button', { name: 'Sign in' }).click();
    // Detects if the page is asking for authorization by identifying the authorize button
    try{
        if (await page1.getByRole('button', {name: 'Authorize Henriquess19'}).isVisible()){
            await page1.getByRole('button', {name: 'Authorize Henriquess19'}).click();
            await page1.waitForNavigation();
        }
    }catch (e) {
        console.log('No authorization needed');
    }
    await page.waitForLoadState('load');
    while (page.url() == 'http://localhost:3000/auth/teacher') {
        await page.waitForTimeout(500);
    }
    await expect(page.url()).toBe('http://localhost:3000/auth/status');
});

test('Login Pending Student', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'Open settings' }).click();
    const page1Promise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Student' }).click();
    const page1 = await page1Promise;
    await page1.getByLabel('Username or email address').fill('i-on-classcode-pending-student@outlook.pt');
    await page1.getByLabel('Password').fill('ClassCodePendingStudent');
    await page1.getByRole('button', { name: 'Sign in' }).click();
    // Detects if the page is asking for authorization by identifying the authorize button
    try{
        if (await page1.getByRole('button', {name: 'Authorize Henriquess19'}).isVisible()){
            await page1.getByRole('button', {name: 'Authorize Henriquess19'}).click();
            await page1.waitForNavigation();
        }
    }catch (e) {
        console.log('No authorization needed');
    }
    await page.waitForLoadState('load');
    while (page.url() == 'http://localhost:3000/auth/student') {
        await page.waitForTimeout(500);
    }
    await expect(page.url()).toBe('http://localhost:3000/auth/verify');
});


test('Check if teacher cookies exist', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/teacher_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the teacher tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
});

test('Check if student cookies exist', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the login tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
});
